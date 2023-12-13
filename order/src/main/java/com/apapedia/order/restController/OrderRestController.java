package com.apapedia.order.restController;

import com.apapedia.order.dto.request.UpdateOrderRequestDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.dto.response.SalesDTO;
import com.apapedia.order.model.Order;
import com.apapedia.order.restService.CartRestService;
import com.apapedia.order.restService.OrderRestService;
import com.apapedia.order.security.jwt.JwtUtils;
import com.apapedia.order.setting.Setting;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderRestController {
    @Autowired
    private OrderRestService orderRestService;
    @Autowired
    private Setting setting;
    @Autowired
    private CartRestService cartRestService;
    @Autowired
    private JwtUtils jwtUtils;

    // Order Service 7: Get Order by customer_id
    @GetMapping("/customer-order")
    public ResponseAPI getOrderByCustomerId(HttpServletRequest request) {
        UUID customerId = null;

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            customerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }

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
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Get Order by Order ID
    @GetMapping("/{orderId}")
    public ResponseAPI getOrderByOrderId(@PathVariable("orderId") UUID orderId) {

        var response = new ResponseAPI<>();
        try {
            var order = orderRestService.findOrderById(orderId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(order);
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        
        return response;
    }

    // Order Service 8: Get order by seller_id
    @GetMapping("/seller-order")
    public ResponseAPI getOrderBySellerId(HttpServletRequest request) {
        UUID sellerId = null;

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            sellerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }
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
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 6: Post Order (Customer)
    @PostMapping("/create-order")
    public ResponseAPI restAddOrder(HttpServletRequest request) {
        var response = new ResponseAPI<>();

        try {
            orderRestService.createRestOrder(request);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult("Successfully create order");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 11: Get daily sales for a seller in the current month
    @GetMapping("/sales-graph")
    public ResponseAPI getDailySalesBySellerId(HttpServletRequest request) {
        UUID sellerId = null;

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            sellerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }
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
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
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
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }
}
