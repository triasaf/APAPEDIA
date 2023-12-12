package com.apapedia.order.restService;

import com.apapedia.order.dto.request.CatalogDTO;
import com.apapedia.order.dto.helper.CatalogQuantityPairDTO;
import com.apapedia.order.dto.request.SubstractCatalogStockDTO;
import com.apapedia.order.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.order.dto.request.UpdateOrderRequestDTO;
import com.apapedia.order.dto.response.ProfileResponseDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.dto.response.SalesDTO;
import com.apapedia.order.exception.InsufficientBalanceException;
import com.apapedia.order.exception.StockNotEnoughException;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.model.Order;
import com.apapedia.order.model.OrderItem;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;
import com.apapedia.order.repository.OrderDb;
import com.apapedia.order.repository.OrderItemDb;
import com.apapedia.order.security.jwt.JwtUtils;
import com.apapedia.order.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderRestServiceImpl implements OrderRestService {
    @Autowired
    private OrderDb orderDb;
    @Autowired
    private OrderItemDb orderItemDb;
    @Autowired
    private CartDb cartDb;
    @Autowired
    private CartItemDb cartItemDb;
    @Autowired
    private CartRestService cartRestService;
    @Autowired
    private Setting setting;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<Order> getAllOrder() {
        return orderDb.findAll();
    }

    @Override
    public Order findOrderById(UUID orderId) {
        for (Order order: getAllOrder()) {
            if(order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    // Order Service 7: Get Order by customer_id
    @Override
    public List<Order> findOrderByCustomerId(UUID customer) {
        var getAllOrder = getAllOrder();
        List<Order> listOfOrder = new ArrayList<>();
        for (Order order : getAllOrder) {
            if (order.getCustomer().equals(customer)) {
                listOfOrder.add(order);
            }
        }
        System.out.println("Order not found for Customer ID: " + customer);
        return listOfOrder;
    }

    // Order Service 8: Get Order by seller_id
    @Override
    public List<Order> findOrderBySellerId(UUID seller) {
        var getAllOrder = getAllOrder();
        List<Order> listOfOrder = new ArrayList<>();
        for (Order order : getAllOrder) {
            if (order.getSeller().equals(seller)) {
                listOfOrder.add(order);
            }
        }
        if(listOfOrder.isEmpty()) {
            System.out.println("Order not found for Seller ID: " + seller);
        }

        return listOfOrder;
    }

    // Order Service 6: Post Order (customer)
    @Override
    @Transactional
    public void createRestOrder(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Get JWT token
            var token = headerAuth.substring(7);

            // Get customer ID
            var customerID = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));

            // Get customer info
            ProfileResponseDTO userInformation = getCustomerProfile(token);

            // Ambil cart dari database
            Cart cart = cartRestService.findCartByUserId(customerID);

            // Group cart items by seller
            Map<UUID, List<CatalogQuantityPairDTO>> cartItemPerSellerMap = groupCartItemBySeller(cart);

            // make orders by seller: 1 seller -> 1 order
            List<Order> orders = makeOrdersPerSeller(cartItemPerSellerMap, customerID);

            int totalPriceAllOrders = getTotalPriceAllOrder(orders);

            // balance validation
            if (userInformation.getBalance() < totalPriceAllOrders) {
                throw new InsufficientBalanceException("Your balance is insufficient");
            }

            // save all Order and OrderItem
            orderDb.saveAll(orders);

            // substract catalog stock
            substractCatalogsStock(cart, token);

            // clear all cart items
            clearCartItems(cart);

            // substract user balance
            var updateBalanceDTO = new UpdateBalanceRequestDTO(customerID, "WITHDRAW", totalPriceAllOrders);
            substractUserBalance(updateBalanceDTO, token);
        } else {
            throw new RestClientException("Unauthorized");
        }
    }

    /**
     * Group cart items per seller & check stock of each catalog
     * @param cart: customer's cart
     * @return Map of seller ID and list of all products going to but and its quantity
     */
    private Map<UUID, List<CatalogQuantityPairDTO>> groupCartItemBySeller(Cart cart) {
        RestTemplate restTemplate = new RestTemplate();
        Map<UUID, List<CatalogQuantityPairDTO>> cartItemPerSellerMap = new HashMap<>();

        for (CartItem cartItem : cart.getListCartItem()) {
            UUID productIdIter = cartItem.getProductId();
            String getCatalogByProductIdURL = setting.CATALOG_SERVER_URL + "/" + productIdIter;

            ResponseEntity<ResponseAPI<CatalogDTO>> catalogDTO = restTemplate.exchange(
                    getCatalogByProductIdURL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });

            if (catalogDTO.getBody() != null && catalogDTO.getBody().getStatus() == 200) {
                if (cartItem.getQuantity() > catalogDTO.getBody().getResult().getStok()) {
                    throw new StockNotEnoughException("Stock for " + catalogDTO.getBody().getResult().getProductName() + " not enough");
                }
                var idSeller = catalogDTO.getBody().getResult().getSeller();

                CatalogQuantityPairDTO catalogPair = new CatalogQuantityPairDTO(catalogDTO.getBody().getResult(), cartItem.getQuantity());

                if (cartItemPerSellerMap.containsKey(idSeller)) {
                    cartItemPerSellerMap.get(idSeller).add(catalogPair);
                } else {
                    // Jika belum ada, buat List baru, tambahkan productIdIter, masukkan ke map
                    List<CatalogQuantityPairDTO> catalogPairList = new ArrayList<>();
                    catalogPairList.add(catalogPair);
                    cartItemPerSellerMap.put(idSeller, catalogPairList);
                }
            }
        }

        return cartItemPerSellerMap;
    }

    private List<Order> makeOrdersPerSeller(Map<UUID, List<CatalogQuantityPairDTO>> cartItemPerSellerMap,
                                            UUID customerID) {
        List<Order> listOrderCreated = new ArrayList<>();

        for (UUID sellerId : cartItemPerSellerMap.keySet()) {
            Order order = new Order();
            order.setListOrderItem(new ArrayList<>());

            order.setSeller(sellerId);
            order.setCustomer(customerID);

            int totalPricePerOrder = 0;

            for (CatalogQuantityPairDTO pairDTO : cartItemPerSellerMap.get(sellerId)) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductPrice(pairDTO.getCatalog().getPrice());
                orderItem.setProductId(pairDTO.getCatalog().getId());
                orderItem.setQuantity(pairDTO.getQuantity());
                orderItem.setProductName(pairDTO.getCatalog().getProductName());
                orderItem.setOrderId(order);

                totalPricePerOrder += pairDTO.getCatalog().getPrice() * pairDTO.getQuantity();

                order.getListOrderItem().add(orderItem);
            }

            order.setTotalPrice(totalPricePerOrder);
            listOrderCreated.add(order);
        }

        return listOrderCreated;
    }

    private int getTotalPriceAllOrder(List<Order> orders) {
        int totalPriceAllOrders = 0;
        for (Order order : orders) {
            totalPriceAllOrders += order.getTotalPrice();
        }
        return totalPriceAllOrders;
    }

    private ProfileResponseDTO getCustomerProfile(String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);

        try {
            ResponseEntity<ResponseAPI<ProfileResponseDTO>> customerProfile = restTemplate.exchange(
                    setting.USER_SERVER_URL + "/me",
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    new ParameterizedTypeReference<>() {}
            );

            if (customerProfile.getBody() != null && customerProfile.getBody().getStatus().equals(200)) {
                return customerProfile.getBody().getResult();
            } else {
                throw new RestClientException(customerProfile.getBody().getError());
            }
        } catch (Exception e) {
            throw new RestClientException("Failed to get user information: " + e.getMessage());
        }
    }

    private void clearCartItems(Cart cart) {
        List<UUID> listCartItemUUID = new ArrayList<>();
        for (CartItem cartItem : cart.getListCartItem()) {
            listCartItemUUID.add(cartItem.getId());
        }

        cart.setListCartItem(new ArrayList<>());

        cartItemDb.deleteByIdIn(listCartItemUUID);
        cart.setTotalPrice(0);
        cartDb.save(cart);
    }

    private void substractUserBalance(UpdateBalanceRequestDTO balanceDTO, String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);

        try {
            ResponseEntity<ResponseAPI> response = restTemplate.exchange(
                    setting.USER_SERVER_URL + "/profile/update-balance",
                    HttpMethod.PUT,
                    new HttpEntity<>(balanceDTO, httpHeaders),
                    new ParameterizedTypeReference<ResponseAPI>() {}
            );

            if (response.getBody() == null || !response.getBody().getStatus().equals(200))  {
                throw new RestClientException("Failed to substract user balance: " + response.getBody().getError());
            }
        } catch (Exception e) {
            throw new RestClientException("Failed to substract user balance: " + e.getMessage());
        }
    }

    private void substractCatalogsStock(Cart cart, String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);

        try {
            for (CartItem cartItem : cart.getListCartItem()) {
                var stockDTO = new SubstractCatalogStockDTO(cartItem.getProductId(), cartItem.getQuantity());

                ResponseEntity<ResponseAPI> response = restTemplate.exchange(
                        setting.CATALOG_SERVER_URL + "/substract-stock",
                        HttpMethod.PUT,
                        new HttpEntity<>(stockDTO, httpHeaders),
                        new ParameterizedTypeReference<>() {}
                );

                if (response.getBody() == null || !response.getBody().getStatus().equals(200))  {
                    throw new RestClientException("Failed to substract user balance: " + response.getBody().getError());
                }
            }
        } catch (Exception e) {
            throw new RestClientException("Failed to substract user balance: " + e.getMessage());
        }
    }

    @Override
    public List<SalesDTO> getDailySalesBySellerId(UUID sellerId) {
        List<Object[]> dailySalesData = orderDb.getDailySalesDataBySellerId(sellerId);

        // Mapping data dari daftar objek menjadi list SalesDTO
        List<SalesDTO> dailySalesList = new ArrayList<>();
        for (Object[] data : dailySalesData) {
            // Mengambil nilai hari dari hasil query
            Integer day = (Integer) data[0];

            // Membuat objek Calendar dan menetapkan tanggal bulan dan tahun tetap
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

            // Konversi ke Date dan tambahkan ke list
            Date date = calendar.getTime();
            Long numberOfProductsSold = (Long) data[1];

            // Konversi ke SalesDTO dan tambahkan ke list
            SalesDTO salesDTO = new SalesDTO(date, numberOfProductsSold.intValue());
            dailySalesList.add(salesDTO);
        }
        return dailySalesList;
    }

    @Override
    public Order changeStatusOrder(UpdateOrderRequestDTO orderDTO) {
        Order order = findOrderById(orderDTO.getId());
        order.setStatus(orderDTO.getStatus());
        orderDb.save(order);

        return order;
    }
}
