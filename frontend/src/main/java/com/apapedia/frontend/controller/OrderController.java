package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.response.order.OrderResponseDTO;
import com.apapedia.frontend.dto.response.order.SalesResponseDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
public class OrderController {
    @Autowired
    private Setting setting;

    // Frontend 6: Order History Page
    @GetMapping("/sales-history")
    public String mySalesHistory(Model model) {
        // TODO: Change to seller logged in
        String sellerId = "924695a5-973d-428a-b5dc-d3bdb0a306f5";

        String getSellerOrderApiUrl = setting.ORDER_SERVER_URL + "/"  + sellerId + "/seller-order";

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

    @GetMapping("/sales-graph")
    public String mySalesGraph(Model model) {
        // TODO: Change to seller logged in
        String sellerId = "e046eb69-27e3-4eba-b75a-e0a0d94791de";

        // Make HTTP Request to get seller order list
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI<List<SalesResponseDTO>>> orderResponse = restTemplate.exchange(
                    Setting.ORDER_SERVER_URL + "/" + sellerId + "/sales-graph",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseAPI<List<SalesResponseDTO>>>() {
                    });

            // Check keberhasilan respons dan menambahkan data ke model
            if (orderResponse.getBody() != null && orderResponse.getBody().getStatus() == 200) {
                List<SalesResponseDTO> orders = orderResponse.getBody().getResult();

                // Buat array untuk label (tanggal) dan data (jumlah produk terjual)
                List<String> labels = new ArrayList<>();
                List<Integer> data = new ArrayList<>();

                // Inisialisasi rentang tanggal (1-30)
                LocalDate currentDate = LocalDate.now();
                int daysInMonth = currentDate.lengthOfMonth();

                // Iterasi melalui setiap hari dalam rentang
                for (int i = 1; i <= daysInMonth; i++) {
                    final int dayOfMonth = i; // Declare dayOfMonth as final

                    String formattedDate = String.valueOf(dayOfMonth);
                    labels.add(formattedDate);

                    // Cari data untuk tanggal tertentu dalam respons dari server
                    Optional<SalesResponseDTO> salesForDate = orders.stream()
                            .filter(order -> order.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    .getDayOfMonth() == dayOfMonth)
                            .findFirst();

                    // Jika data ditemukan, tambahkan nilai numberOfProductsSold, jika tidak,
                    // tambahkan 0
                    if (salesForDate.isPresent()) {
                        data.add(salesForDate.get().getNumberOfProductsSold());
                    } else {
                        data.add(0);
                    }
                }

                // Tambahkan data ke model
                model.addAttribute("labels", labels);
                model.addAttribute("data", data);
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "order/sales-graph";
    }

}
