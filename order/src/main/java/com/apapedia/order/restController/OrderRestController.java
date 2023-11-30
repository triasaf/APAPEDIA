package com.apapedia.order.restController;

import com.apapedia.order.dto.request.CatalogDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.model.Order;
import com.apapedia.order.model.OrderItem;
import com.apapedia.order.restService.CartRestService;
import com.apapedia.order.restService.OrderRestService;
import com.apapedia.order.setting.Setting;

import jakarta.validation.Valid;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderRestController {
    @Autowired
    private OrderRestService orderRestService;


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

        // Buat order baru
        var order = new Order();

        RestTemplate restTemplate = new RestTemplate();
        // Ambil cart dari database
        Cart cart = cartRestService.findCartByUserId(customerId);

        if (cart == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Cart not found for customer ID: " + customerId);
            return response;
        }

        // Ambil uuid seller dari productId
        UUID productId = null;
        // Get productId (TODO: Cari cara yang lebih efisien)
        for (CartItem cartItem : cart.getListCartItem()) {
            productId = cartItem.getProductId();
        }

        String getCatalogByProductId = Setting.CATALOG_SERVER_URL + "/" + productId;

        if (!cart.getListCartItem().isEmpty()) {
            try {
                ResponseEntity<ResponseAPI<CatalogDTO>> catalogDTO = restTemplate.exchange(
                        getCatalogByProductId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseAPI<CatalogDTO>>() {
                        });
                if (catalogDTO.getBody() != null && catalogDTO.getBody().getStatus() == 200) {
                    // Set seller, customer, totalPrice
                    order.setSeller(catalogDTO.getBody().getResult().getSeller());
                    order.setTotalPrice(cart.getTotalPrice());
                    order.setCustomer(customerId);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            // Convert cartItem menjadi orderItem
            List<OrderItem> listOrderItems = new ArrayList<>();
            for (CartItem cartItem : cart.getListCartItem()) {
                try {
                    String getCatalogByProductIdLoop = Setting.CATALOG_SERVER_URL + "/" + cartItem.getProductId();
                    ResponseEntity<ResponseAPI<CatalogDTO>> catalogDTO = restTemplate.exchange(
                            getCatalogByProductIdLoop,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ResponseAPI<CatalogDTO>>() {
                            });
                    if (catalogDTO.getBody() != null && catalogDTO.getBody().getStatus() == 200) {
                        // Buat elemen baru untuk orderItem
                        OrderItem orderItem = new OrderItem();

                        // Assign nilai orderItem sesuai data cartItem
                        orderItem.setProductId(cartItem.getProductId());
                        orderItem.setOrderId(order);
                        orderItem.setQuantity(cartItem.getQuantity());

                        // productName & productPrice
                        orderItem.setProductName(catalogDTO.getBody().getResult().getProductName());
                        orderItem.setProductPrice(catalogDTO.getBody().getResult().getPrice());

                        // Menambahkan ke array listOrderItems
                        listOrderItems.add(orderItem);

                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            // set nilai listOrderItem
            order.setListOrderItem(listOrderItems);

            // Save ke database
            orderRestService.createRestOrder(order);

        }

        response.setResult(orderRestService.createRestOrder(order));
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.name());
        return response;
    }
}
