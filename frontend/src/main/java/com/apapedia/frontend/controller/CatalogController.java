package com.apapedia.frontend.controller;

import java.io.IOException;
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

import com.apapedia.frontend.dto.request.catalog.CreateCatalogRequestDTO;
import com.apapedia.frontend.dto.request.catalog.UpdateCatalogRequestDTO;
import com.apapedia.frontend.dto.request.catalog.CategoryDTO;
import com.apapedia.frontend.dto.response.catalog.ReadCatalogResponseDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.setting.Setting;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CatalogController {

    @GetMapping("/my-catalog/add-product")
    public String addProductPage(Model model, RedirectAttributes redirectAttributes) {
        String getAllCategoryApiUrl = Setting.CATEGORY_SERVER_URL + "/all";
        // Make HTTP request to get all categories
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ResponseAPI<List<CategoryDTO>>> categoryResponse = restTemplate.exchange(
                getAllCategoryApiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        // Check if the response is successful and contains categories
        if (categoryResponse.getBody() != null && categoryResponse.getBody().getStatus() == 200) {
            model.addAttribute("categories", categoryResponse.getBody().getResult());
        } else {
            // Handle the case where the request fails or the response does not contain
            // categories
            redirectAttributes.addFlashAttribute("error", "No category available, try again later");
            return "redirect:/my-catalog";
        }

        CreateCatalogRequestDTO catalogRequest = new CreateCatalogRequestDTO();
        model.addAttribute("catalogDTO", catalogRequest);
        return "catalog/add-product";
    }

    @PostMapping("/my-catalog/add-product")
    public String addProduct(@ModelAttribute CreateCatalogRequestDTO catalogDTO,
            RedirectAttributes redirectAttributes,
            Model model) throws IOException {
        // TODO: set seller id to seller loggedin id
        catalogDTO.setSeller(UUID.fromString("b79cf161-ff78-4c84-a9bd-30dc4fd721a1"));
        catalogDTO.setImage(catalogDTO.getImageFile().getBytes());

        // Create a HttpHeaders object to set the content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateCatalogRequestDTO> requestEntity = new HttpEntity<>(catalogDTO, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResponseAPI<ReadCatalogResponseDTO>> response = restTemplate.exchange(
                Setting.CATALOG_SERVER_URL + "/add",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
            redirectAttributes.addFlashAttribute("success", "New product added successfully");
            return "redirect:/my-catalog";
        } else {
            model.addAttribute("error", response.getBody().getError());
            model.addAttribute("catalogDTO", catalogDTO);
            return "catalog/add-product";
        }
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
                new ParameterizedTypeReference<>() {
                });
        if (catalogResponse.getBody() != null && catalogResponse.getBody().getStatus().equals(200)) {
            model.addAttribute("catalog", catalogResponse.getBody().getResult());
        } else {
            model.addAttribute("error", "Catalog not found, try again later");
        }

        // Mendapatkan daftar kategori untuk dropdown
        ResponseEntity<ResponseAPI<List<CategoryDTO>>> categories = restTemplate.exchange(
                Setting.CATEGORY_SERVER_URL + "/all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });
        if (categories.getBody() != null && categories.getBody().getStatus().equals(200)) {
            model.addAttribute("categories", categories.getBody().getResult());
        } else {
            model.addAttribute("error", "List of categories not found, try again later");
        }

        return "catalog/update-product";
    }

    @PostMapping("/my-catalog/update-product")
    public String updateProduct(@ModelAttribute UpdateCatalogRequestDTO catalogDTO, Model model,
            RedirectAttributes redirectAttributes) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateCatalogRequestDTO> requestEntity = new HttpEntity<>(catalogDTO, headers);
        RestTemplate restTemplate = new RestTemplate();

        if (catalogDTO.getImageFile() != null && !catalogDTO.getImageFile().getOriginalFilename().isBlank()) {
            catalogDTO.setImage(catalogDTO.getImageFile().getBytes());
        }

        ResponseEntity<ResponseAPI<ReadCatalogResponseDTO>> response = restTemplate.exchange(
                Setting.CATALOG_SERVER_URL + "/update",
                HttpMethod.PUT,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
            return "redirect:/my-catalog";
        } else {
            redirectAttributes.addFlashAttribute("error", response.getBody().getError());
            return "redirect:/my-catalog/" + catalogDTO.getId() + "/update-product";
        }
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
            // GENERATE API URL
            String catalogUrl = Setting.CATALOG_SERVER_URL;
            String encodeCategory = category.replaceAll(" ", "-").replaceAll("&", "n").toLowerCase();
            if ((startPrice != null && endPrice != null)
                    || (!encodeCategory.isBlank() && !encodeCategory.equals("all"))) {
                catalogUrl += "/filter";
                if (startPrice != null && endPrice != null) {
                    catalogUrl += "?startPrice=" + startPrice + "&endPrice=" + endPrice;

                    model.addAttribute("startPrice", startPrice);
                    model.addAttribute("endPrice", endPrice);
                    if (!encodeCategory.isBlank() && !encodeCategory.equals("all")) {
                        catalogUrl += "&categoryName=" + encodeCategory;
                        model.addAttribute("selectedCategory", category);
                    }
                } else if (!encodeCategory.isBlank()) {
                    catalogUrl += "?categoryName=" + encodeCategory;
                    model.addAttribute("selectedCategory", category);
                }
            } else {
                // Default tanpa filter
                catalogUrl += "/all";
            }

            // GET LIST OF CATALOG
            ResponseEntity<ResponseAPI<List<ReadCatalogResponseDTO>>> catalogsResponse = restTemplate.exchange(
                    catalogUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            if (catalogsResponse.getBody() != null && catalogsResponse.getBody().getStatus() == 200) {
                List<ReadCatalogResponseDTO> catalogs = catalogsResponse.getBody().getResult();
                model.addAttribute("imageURL", "http://localhost:8081/api/catalog/image/");
                model.addAttribute("catalogs", catalogs);
            } else {
                model.addAttribute("error", catalogsResponse.getBody().getError());
            }

            // GET LIST OF CATEGORY
            ResponseEntity<ResponseAPI<List<CategoryDTO>>> resultCategory = restTemplate.exchange(
                    Setting.CATEGORY_SERVER_URL + "/all",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            if (resultCategory.getBody() != null && resultCategory.getBody().getStatus() == 200) {
                List<CategoryDTO> categories = resultCategory.getBody().getResult();
                model.addAttribute("categories", categories);
            } else {
                model.addAttribute("error", resultCategory.getBody().getError());
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "catalog/my-catalog";
    }

    @GetMapping("/my-catalog/{productId}")
    public String detailProduct(@PathVariable("productId") UUID productId, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseAPI<ReadCatalogResponseDTO>> catalogResponse = restTemplate.exchange(
                    Setting.CATALOG_SERVER_URL + "/" + productId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            if (catalogResponse.getBody() != null && catalogResponse.getBody().getStatus().equals(200)) {
                model.addAttribute("imageURL", "http://localhost:8081/api/catalog/image/");
                model.addAttribute("catalog", catalogResponse.getBody().getResult());
            } else {
                model.addAttribute("error", "Catalog not found, try again later");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "catalog/detail-catalog";
    }

    @GetMapping("/my-catalog/{productId}/delete-product")
    public String deleteProduct(@PathVariable("productId") UUID productId, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Menghapus produk berdasarkan ID
            ResponseEntity<ResponseAPI<String>> response = restTemplate.exchange(
                    Setting.CATALOG_SERVER_URL + "/" + productId + "/delete",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
                redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", response.getBody().getError());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/my-catalog";
    }

}