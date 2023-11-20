package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.request.order.OrderResponseDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.setting.Setting;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class OrderController {

    // Frontend 6: Order History Page
    @GetMapping("/sales-history")
    public String mySalesHistory(Model model) {
        //TODO: Change to seller logged in
        String sellerId = "924695a5-973d-428a-b5dc-d3bdb0a306f5";

        String getSellerOrderApiUrl = Setting.ORDER_SERVER_URL + "/"  + sellerId + "/seller-order";

        // Make HTTP Request to get seller order list
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI<List<OrderResponseDTO>>> orderResponse = restTemplate.exchange(
                    getSellerOrderApiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseAPI<List<OrderResponseDTO>>>() {
                    });

            // Check keberhasilan respons dan menambahkan data ke model
            if (orderResponse.getBody() != null && orderResponse.getBody().getStatus() == 200) {
                model.addAttribute("orders", orderResponse.getBody().getResult());
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "order/sales-history";
    }
}
