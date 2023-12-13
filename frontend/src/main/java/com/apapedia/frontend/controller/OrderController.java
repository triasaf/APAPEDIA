package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.response.order.OrderResponseDTO;
import com.apapedia.frontend.dto.response.order.SalesResponseDTO;
import com.apapedia.frontend.dto.request.order.UpdateOrderRequestDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.security.jwt.JwtUtils;
import com.apapedia.frontend.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {
    @Autowired
    private Setting setting;
    @Autowired
    private JwtUtils jwtUtils;

    // Frontend 6: Order History Page
    @GetMapping("/sales-history")
    public String mySalesHistory(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String jwtToken = null;
        String sellerId = null;

        if (session != null)
            jwtToken = (String) session.getAttribute("token");

        HttpHeaders headers = new HttpHeaders();
        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");
            sellerId = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");

            model.addAttribute("username", username);
            model.addAttribute("name", name);
            headers.set("Authorization", "Bearer " + jwtToken);
        }

        // Make HTTP Request to get seller order list
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI<List<OrderResponseDTO>>> orderResponse = restTemplate.exchange(
                    setting.ORDER_SERVER_URL + "/seller-order",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<ResponseAPI<List<OrderResponseDTO>>>() {
                    });
            // Check keberhasilan respons dan menambahkan data ke model
            if (orderResponse.getBody() != null && orderResponse.getBody().getStatus() == 200) {
                model.addAttribute("orders", orderResponse.getBody().getResult());
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        try {
            ResponseEntity<ResponseAPI<List<SalesResponseDTO>>> orderResponse = restTemplate.exchange(
                    setting.ORDER_SERVER_URL + "/sales-graph",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
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

        try {
            ResponseEntity<ResponseAPI<List<SalesResponseDTO>>> orderResponse = restTemplate.exchange(
                    setting.ORDER_SERVER_URL + "/" + sellerId + "/sales-graph",
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

        try {
            ResponseEntity<ResponseAPI<List<SalesResponseDTO>>> orderResponse = restTemplate.exchange(
                    setting.ORDER_SERVER_URL + "/" + sellerId + "/sales-graph",
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

        return "order/sales-history";
    }

    @GetMapping("/sales-graph")
    public String mySalesGraph(Model model) {
        // TODO: Change to seller logged in

        String sellerId = "b79cf161-ff78-4c84-a9bd-30dc4fd721a1";

        // Make HTTP Request to get seller order list
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<ResponseAPI<List<SalesResponseDTO>>> orderResponse = restTemplate.exchange(
                    setting.ORDER_SERVER_URL + "/" + sellerId + "/sales-graph",
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

    @GetMapping("/sales-history/{orderId}")
    public String orderHistoryDetail(@PathVariable("orderId") UUID orderId, Model model, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null)
            jwtToken = (String) session.getAttribute("token");
        if (jwtToken != null && !jwtToken.isBlank()) {
            headers.set("Authorization", "Bearer " + jwtToken);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseAPI<OrderResponseDTO>> orderResponse = restTemplate.exchange(
                    setting.ORDER_SERVER_URL + "/" + orderId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            if (orderResponse.getBody() != null && orderResponse.getBody().getStatus().equals(200)) {
                model.addAttribute("order", orderResponse.getBody().getResult());
            } else {
                model.addAttribute("error", "Order not found for Order ID: " + orderId);
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        if (session != null)
            jwtToken = (String) session.getAttribute("token");
        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");

            model.addAttribute("username", username);
            model.addAttribute("name", name);
        }

        return "order/detail-order";
    }

    @GetMapping("/sales-history/{orderId}/update-status")
    public String updateOrderStatus(@PathVariable("orderId") UUID orderId, RedirectAttributes redirectAttributes,
            HttpServletRequest request, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null)
            jwtToken = (String) session.getAttribute("token");
        if (jwtToken != null && !jwtToken.isBlank()) {
            headers.set("Authorization", "Bearer " + jwtToken);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseAPI<OrderResponseDTO>> orderResponse = restTemplate.exchange(
                    setting.ORDER_SERVER_URL + "/" + orderId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            if (orderResponse.getBody() != null && orderResponse.getBody().getStatus().equals(200)) {



                Integer updatedStatus = orderResponse.getBody().getResult().getStatus() + 1;
                UpdateOrderRequestDTO updateOrderDTO = new UpdateOrderRequestDTO(orderId, updatedStatus);

                HttpEntity<UpdateOrderRequestDTO> requestEntity = new HttpEntity<>(updateOrderDTO, headers);

                try {
                    // Menghapus produk berdasarkan ID
                    ResponseEntity<ResponseAPI<OrderResponseDTO>> response = restTemplate.exchange(
                            setting.ORDER_SERVER_URL + "/change-status",
                            HttpMethod.PUT,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

                    if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
                        redirectAttributes.addFlashAttribute("success", "Order status updated successfully");
                    } else {
                        redirectAttributes.addFlashAttribute("error", response.getBody().getError());
                    }
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", e.getMessage());
                }
            } else {
                model.addAttribute("error", "Order not found for Order ID: " + orderId);
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/sales-history/" + orderId;
    }

}
