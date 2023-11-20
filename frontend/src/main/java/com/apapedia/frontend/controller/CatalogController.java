package com.apapedia.frontend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apapedia.frontend.dto.request.catalog.CategoryResponseDTO;
import com.apapedia.frontend.dto.request.catalog.CreateCatalogRequestDTO;
import com.apapedia.frontend.dto.request.catalog.UpdateCatalogRequestDTO;
import com.apapedia.frontend.dto.request.catalog.CategoryDTO;
import com.apapedia.frontend.dto.response.ReadCatalogResponseDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.setting.Setting;

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

    @GetMapping("/my-catalog/{productId}/update-product")
    public String updateProductForm(@PathVariable("productId") UUID productId, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Mendapatkan informasi produk untuk ditampilkan di form
        ResponseEntity<ResponseAPI<UpdateCatalogRequestDTO>> catalogResponse = restTemplate.exchange(
                Setting.CATALOG_SERVER_URL + "/" + productId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ResponseAPI<UpdateCatalogRequestDTO>>() {
                });

        model.addAttribute("catalog", catalogResponse.getBody().getResult());

        // Mendapatkan daftar kategori untuk dropdown
        ResponseEntity<ResponseAPI<List<CategoryDTO>>> categories = restTemplate.exchange(
                Setting.CATEGORY_SERVER_URL + "/all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ResponseAPI<List<CategoryDTO>>>() {
                });
        model.addAttribute("categories", categories.getBody().getResult());

        return "catalog/update-product";
    }

    @PostMapping("/my-catalog/update-product")
    public String updateProduct(@ModelAttribute UpdateCatalogRequestDTO catalogDTO, Model model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Use the catalogDTO in the request body
        HttpEntity<UpdateCatalogRequestDTO> requestEntity = new HttpEntity<>(catalogDTO, headers);

        RestTemplate restTemplate = new RestTemplate();

        // Make the PUT request
        ResponseEntity<ResponseAPI> responseEntity = restTemplate.exchange(
                Setting.CATALOG_SERVER_URL + "/update",
                HttpMethod.PUT,
                requestEntity,
                ResponseAPI.class);

        String status;
        String message;
        if (responseEntity.getBody().getStatus().equals(200)) {
            System.out.println("Product updated successfully!");
            status = "Success";
            message = "Product updated successfully!";
        } else {
            status = responseEntity.getBody().getError();
            message = "Failed to update product. Status code: " + responseEntity.getBody().getStatus();
        }

        model.addAttribute("status", status);
        model.addAttribute("message", message);
        return "message/response-message";
    }

    @GetMapping("/my-catalog")
    public String myCatalog(Model model,
            @RequestParam(value = "category", required = false, defaultValue = "all") String category,
            @RequestParam(value = "startPrice", required = false) Integer startPrice,
            @RequestParam(value = "endPrice", required = false) Integer endPrice) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Menerapkan filter harga dan/atau kategori jika startPrice, endPrice, dan
            // category ada
            String catalogUrl = Setting.CATALOG_SERVER_URL;
            String encodeCategory = category.replaceAll(" ", "-").replaceAll("&", "n").toLowerCase();
            if ((startPrice != null && endPrice != null) || (encodeCategory != null && !encodeCategory.equals("all"))) {
                catalogUrl += "/filter";
                if (startPrice != null && endPrice != null) {
                    catalogUrl += "?startPrice=" + startPrice + "&endPrice=" + endPrice;
                    if (encodeCategory != null && !encodeCategory.equals("all")) {
                        catalogUrl += "&categoryName=" + encodeCategory;
                    }
                } else if (encodeCategory != null && !encodeCategory.equals("all")) {
                    catalogUrl += "?categoryName=" + encodeCategory;
                }
            } else {
                // Default tanpa filter
                catalogUrl += "/all";
            }

            ResponseEntity<ResponseAPI> result = restTemplate.exchange(
                    catalogUrl,
                    HttpMethod.GET,
                    entity,
                    ResponseAPI.class);

            if (result.getBody() != null && result.getBody().getStatus() == 200) {
                List<ReadCatalogResponseDTO> catalogs = (List<ReadCatalogResponseDTO>) result.getBody().getResult();
                model.addAttribute("catalogs", catalogs);

                ResponseEntity<ResponseAPI> resultCategory = restTemplate.exchange(
                        Setting.CATEGORY_SERVER_URL + "/all",
                        HttpMethod.GET,
                        entity,
                        ResponseAPI.class);

                if (resultCategory.getBody() != null && resultCategory.getBody().getStatus() == 200) {
                    List<CategoryDTO> categories = (List<CategoryDTO>) resultCategory.getBody().getResult();
                    model.addAttribute("categories", categories);
                } else {
                    model.addAttribute("error", resultCategory.getBody().getError());
                }

            } else {
                model.addAttribute("error", result.getBody().getError());
            }
        } catch (RestClientException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "catalog/my-catalog";
    }

}