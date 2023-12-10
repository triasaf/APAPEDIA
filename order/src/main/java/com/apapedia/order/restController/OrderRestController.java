package com.apapedia.order.restController;

import com.apapedia.order.dto.request.CatalogDTO;
<<<<<<< HEAD
import com.apapedia.order.dto.request.UpdateOrderRequestDTO;
=======
>>>>>>> b47d3e8c80ab8bd5e4d9f7ae60b6cf6f8bcb2208
import com.apapedia.order.dto.request.CatalogQuantityPairDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.dto.response.SalesDTO;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.model.Order;
import com.apapedia.order.model.OrderItem;
import com.apapedia.order.restService.CartRestService;
import com.apapedia.order.restService.OrderRestService;
import com.apapedia.order.setting.Setting;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import jakarta.servlet.http.HttpServletRequest;
>>>>>>> b47d3e8c80ab8bd5e4d9f7ae60b6cf6f8bcb2208
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderRestController {
    @Autowired
    private OrderRestService orderRestService;
    @Autowired
    private Setting setting;
    @Autowired
    private CartRestService cartRestService; // Replace with API base URL

    // Order Service 7: Get Order by customer_id
    @GetMapping("/{id}/customer-order")
    public ResponseAPI getOrderByCustomerId(@PathVariable(value = "id") UUID customerId) {
        var response = new ResponseAPI<>();
        try {
            var customerOrder = orderRestService.findOrderByCustomerId(customerId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(customerOrder);
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 8: Get order by seller_id
    @GetMapping("/{id}/seller-order")
    public ResponseAPI getOrderBySellerId(@PathVariable(value = "id") UUID sellerId) {
        var response = new ResponseAPI<>();
        try {
            var sellerOrder = orderRestService.findOrderBySellerId(sellerId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(sellerOrder);
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 6: Post Order (Customer)
    @PostMapping("/{id}/create-order")
    public ResponseAPI restAddOrder(@PathVariable("id") UUID customerId) {

        var response = new ResponseAPI<>();
        RestTemplate restTemplate = new RestTemplate();
        // Ambil cart dari database
        Cart cart = cartRestService.findCartByUserId(customerId);

        if (cart == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError("Cart not found for customer ID: " + customerId);
            return response;
        }

        Map<UUID, List<CatalogQuantityPairDTO>> cartItemPerSellerMap = new HashMap<>();

        try {
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

            //For loop untuk membuat Order object per seller
            for (UUID sellerId : cartItemPerSellerMap.keySet()) {
                Order order = new Order();
                order.setListOrderItem(new ArrayList<>());

                order.setSeller(sellerId);
                order.setCustomer(customerId);

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
                orderRestService.createRestOrder(order);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setError("Orders created successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    // Order Service 11: Get daily sales for a seller in the current month
    @GetMapping("/{id}/sales-graph")
    public ResponseAPI getDailySalesBySellerId(@PathVariable(value = "id") UUID sellerId) {
        var response = new ResponseAPI<>();
        try {
            List<SalesDTO> dailySales = orderRestService.getDailySalesBySellerId(sellerId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(dailySales);
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 9: PUT status order 
    @PutMapping("/change-status")
    public ResponseAPI changeStatusOrder(@Valid @RequestBody UpdateOrderRequestDTO orderDTO, 
    BindingResult bindingResult) {
        var response = new ResponseAPI<>();

        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() - 1)
                    res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        
        try {
            Order updatedOrder = orderRestService.changeStatusOrder(orderDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(updatedOrder);
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        }
        return response;
    }
}
