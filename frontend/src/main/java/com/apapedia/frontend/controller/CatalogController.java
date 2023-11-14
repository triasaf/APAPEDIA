package com.apapedia.frontend.controller;

import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.apapedia.frontend.dto.request.catalog.CategoryResponseDTO;
import com.apapedia.frontend.dto.request.catalog.CreateCatalogRequestDTO;

@Controller
public class CatalogController {

    private final String catalogAPIBaseUrl = "http://localhost:8081"; // Replace with API base URL

    @GetMapping("/my-catalog/add-product")
    public String addProductPage(Model model) {
        String getAllCategoryApiUrl = catalogAPIBaseUrl + "/api/category/all";
        // Make HTTP request to get all categories
        RestTemplate restTemplate = new RestTemplate();
        CategoryResponseDTO categoryResponse = restTemplate.getForObject(getAllCategoryApiUrl,
                CategoryResponseDTO.class);

        // Check if the response is successful and contains categories
        if (categoryResponse != null && categoryResponse.getStatus() == 200) {
            model.addAttribute("categories", categoryResponse.getResult());
        } else {
            // Handle the case where the request fails or the response does not contain
            // categories
            model.addAttribute("categories", null);
        }

        CreateCatalogRequestDTO catalogRequest = new CreateCatalogRequestDTO();
        model.addAttribute("catalogRequest", catalogRequest);
        return "catalog/add-product";
    }

    @PostMapping("/my-catalog/add-product")
    public String addProduct(@ModelAttribute CreateCatalogRequestDTO catalogRequest, Model model) {
        // TODO: set seller id to seller loggedin id
        catalogRequest.setSeller(UUID.fromString("b79cf161-ff78-4c84-a9bd-30dc4fd721a1"));
        System.out.println(catalogRequest.toString());

        // Create a HttpHeaders object to set the content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HttpEntity with headers and body
        HttpEntity<CreateCatalogRequestDTO> requestEntity = new HttpEntity<>(catalogRequest, headers);

        // Create a RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Make the POST request
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                catalogAPIBaseUrl + "/api/catalog/add",
                HttpMethod.POST,
                requestEntity,
                String.class);

        String status;
        String message;
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("Product added successfully!");
            status = "Success";
            message = "Product added successfully!";
        } else {
            System.err.println("Failed to add product. Status code: " + responseEntity.getStatusCode());
            status = "Failed";
            message = "Failed to add product. Status code: " + responseEntity.getStatusCode();
        }

        model.addAttribute("status", status);
        model.addAttribute("message", message);
        return "message/response-message";

    }
}
