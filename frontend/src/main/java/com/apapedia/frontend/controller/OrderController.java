package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.request.order.OrderDTO;
import com.apapedia.frontend.dto.request.order.OrderItemDTO;
import com.apapedia.frontend.dto.request.order.OrderResponseDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {

    private final String orderAPIBaseUrl = "http://localhost:8080"; // Replace with API base URL

    // Order 8: Get order by seller_id
    @GetMapping("/seller/order-list")
    public String getOrderListBySellerId(@RequestParam("sellerId") UUID sellerId, Model model) {
        String getSellerOrderApiUrl = orderAPIBaseUrl + "/api/order/" + sellerId + "/seller-order";

        // Make HTTP Request to get seller order list
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResponseAPI> orderResponse = restTemplate.getForEntity(getSellerOrderApiUrl,
                ResponseAPI.class);

        // Check keberhasilan respons dan menambahkan data ke model
        if (orderResponse != null && orderResponse.getBody().getStatus() == 200) {
            model.addAttribute("orders",orderResponse.getBody().getResult());
        }else{
            // Menangani kasus jika request gagal, atau response tidak berisi pesan
            model.addAttribute("orders",null);
        }

        return "order/seller-order-list";

    }
}
