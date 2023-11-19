package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.response.ResponseAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class OrderController {

    private final String orderAPIBaseUrl = "http://localhost:8080"; // Replace with API base URL

    // Order 7: Get Order by customer_id
    @GetMapping("/customer/order-list")
    public String getOrderListByCustomerId(@RequestParam("customerId") UUID customerId, Model model) {
        String getCustomerOrderApiUrl = orderAPIBaseUrl + "/api/order/" + customerId + "/customer-order";

        // Make HTTP Request to get customer order list
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResponseAPI> orderResponse = restTemplate.getForEntity(getCustomerOrderApiUrl,
                ResponseAPI.class);

        // Check keberhasilan respons dan menambahkan data ke model
        if (orderResponse.getBody() != null && orderResponse.getBody().getStatus() == 200) {
            model.addAttribute("orders",orderResponse.getBody().getResult());
        }else{
            // Menangani kasus jika request gagal, atau response tidak berisi pesan
            model.addAttribute("orders",null);
        }
        return "order/customer-order-list";
    }

    // Order 8: Get order by seller_id
    @GetMapping("/seller/order-list")
    public String getOrderListBySellerId(@RequestParam("sellerId") UUID sellerId, Model model) {
        String getSellerOrderApiUrl = orderAPIBaseUrl + "/api/order/" + sellerId + "/seller-order";

        // Make HTTP Request to get seller order list
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResponseAPI> orderResponse = restTemplate.getForEntity(getSellerOrderApiUrl,
                ResponseAPI.class);

        // Check keberhasilan respons dan menambahkan data ke model
        if (orderResponse.getBody() != null && orderResponse.getBody().getStatus() == 200) {
            model.addAttribute("orders", orderResponse.getBody().getResult());
        } else {
            // Menangani kasus jika request gagal, atau response tidak berisi pesan
            model.addAttribute("orders", null);
        }

        return "order/seller-order-list";
    }

    @GetMapping("/sales-history")
    public String salesHistory(Model model) {
        List<List<String>> temp = new ArrayList<>(Arrays.asList(
                Arrays.asList("fachryanwar", "26-08-2023", "Waiting", "Rp 50.000,00"),
                Arrays.asList("mazayanur", "02-07-2023", "Waiting", "Rp 50.000,00")
        ));
        model.addAttribute("temp", temp);
        return "order/sales-history";
    }
}
