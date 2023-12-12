package com.apapedia.frontend.controller;

import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.dto.response.catalog.ReadCatalogResponseDTO;
import com.apapedia.frontend.security.jwt.JwtUtils;
import com.apapedia.frontend.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BaseController {
    @Autowired
    Setting setting;
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        List<ReadCatalogResponseDTO> catalogs = new ArrayList<>();

        //TODO: will be deleted
        System.out.println(setting.USER_SERVER_URL + "/register");
        System.out.println(setting.CATALOG_SERVER_URL);
        System.out.println(setting.CATEGORY_SERVER_URL);
        System.out.println(setting.ORDER_SERVER_URL);
        System.out.println(setting.CART_SERVER_URL);

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");

        var catalogUrl = setting.CATALOG_SERVER_URL;

        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");
            var id = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");

            catalogUrl += "/by-seller/" + id;

            model.addAttribute("username", username);
            model.addAttribute("name", name);
        } else {
            catalogUrl += "/all";
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseAPI<List<ReadCatalogResponseDTO>>> catalogResponse = restTemplate.exchange(
                    catalogUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (catalogResponse.getBody() != null && catalogResponse.getBody().getStatus().equals(200)) {
                catalogs = catalogResponse.getBody().getResult();
            }
        } catch (Exception ignored) {}

        model.addAttribute("listCatalog", catalogs);
        model.addAttribute("imageURL", "http://apap-189.cs.ui.ac.id/api/catalog/image/");
        return "home";
    }
}
