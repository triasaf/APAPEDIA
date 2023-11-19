package com.apapedia.order.restController;

import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.restService.OrderRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderRestController {
    @Autowired
    private OrderRestService orderRestService;


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
}
