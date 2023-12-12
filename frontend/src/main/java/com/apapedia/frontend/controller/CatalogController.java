package com.apapedia.frontend.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apapedia.frontend.dto.request.catalog.CategoryDTO;
import com.apapedia.frontend.dto.request.catalog.CreateCatalogRequestDTO;
import com.apapedia.frontend.dto.request.catalog.UpdateCatalogRequestDTO;
import com.apapedia.frontend.dto.response.ResponseAPI;
import com.apapedia.frontend.dto.response.catalog.ReadCatalogResponseDTO;
import com.apapedia.frontend.security.jwt.JwtUtils;
import com.apapedia.frontend.setting.Setting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CatalogController {
    @Autowired
    private Setting setting;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/catalog/add-product")
    public String addProductPage(Model model, RedirectAttributes redirectAttributes) {
        String getAllCategoryApiUrl = setting.CATEGORY_SERVER_URL + "/all";
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
            return "redirect:/catalog";
        }

        CreateCatalogRequestDTO catalogRequest = new CreateCatalogRequestDTO();
        model.addAttribute("catalogDTO", catalogRequest);
        return "catalog/add-product";
    }

    @PostMapping("/catalog/add-product")
    public String addProduct(@ModelAttribute CreateCatalogRequestDTO catalogDTO,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) throws IOException {

        try {
            catalogDTO.setImage(catalogDTO.getImageFile().getBytes());

            // Create a HttpHeaders object to set the content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpSession session = request.getSession(false);
            String jwtToken = null;
            if (session != null) jwtToken = (String) session.getAttribute("token");
            if (jwtToken != null && !jwtToken.isBlank()) {
                headers.set("Authorization", "Bearer " + jwtToken);
                var id = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");
                catalogDTO.setSeller(UUID.fromString(id));
            }

            HttpEntity<CreateCatalogRequestDTO> requestEntity = new HttpEntity<>(catalogDTO, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseAPI<ReadCatalogResponseDTO>> response = restTemplate.exchange(
                    setting.CATALOG_SERVER_URL + "/add",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    });

            if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
                redirectAttributes.addFlashAttribute("success", "New product added successfully");
                return "redirect:/catalog";
            } else {
                model.addAttribute("error", response.getBody().getError());
                model.addAttribute("catalogDTO", catalogDTO);
                return "catalog/add-product";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("catalogDTO", catalogDTO);
            return "catalog/add-product";
        }
    }

    @GetMapping("/catalog/{productId}/update-product")
    public String updateProductForm(@PathVariable("productId") UUID productId, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Mendapatkan informasi produk untuk ditampilkan di form
        ResponseEntity<ResponseAPI<UpdateCatalogRequestDTO>> catalogResponse = restTemplate.exchange(
                setting.CATALOG_SERVER_URL + "/" + productId,
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
                setting.CATEGORY_SERVER_URL + "/all",
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

    @PostMapping("/catalog/update-product")
    public String updateProduct(
            @ModelAttribute UpdateCatalogRequestDTO catalogDTO,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) throws IOException {

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (jwtToken != null && !jwtToken.isBlank()) {
            headers.set("Authorization", "Bearer " + jwtToken);
            var id = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");

            if (!catalogDTO.getSeller().equals(UUID.fromString(id))) {
                redirectAttributes.addFlashAttribute("error", "You do not have access to update this product");
                return "redirect:/catalog/" + catalogDTO.getId() + "/update-product";
            }
        }
        HttpEntity<UpdateCatalogRequestDTO> requestEntity = new HttpEntity<>(catalogDTO, headers);
        RestTemplate restTemplate = new RestTemplate();

        if (catalogDTO.getImageFile() != null &&
                catalogDTO.getImageFile().getOriginalFilename() != null &&
                !catalogDTO.getImageFile().getOriginalFilename().isBlank()) {
            catalogDTO.setImage(catalogDTO.getImageFile().getBytes());
        }

        ResponseEntity<ResponseAPI<ReadCatalogResponseDTO>> response = restTemplate.exchange(
                setting.CATALOG_SERVER_URL + "/update",
                HttpMethod.PUT,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
            return "redirect:/catalog/" + catalogDTO.getId();
        } else {
            redirectAttributes.addFlashAttribute("error", response.getBody().getError());
            return "redirect:/catalog/" + catalogDTO.getId() + "/update-product";
        }
    }

    @GetMapping("/catalog")
    public String myCatalog(Model model,
                            @RequestParam(value = "category", required = false, defaultValue = "all") String category,
                            @RequestParam(value = "startPrice", required = false) Integer startPrice,
                            @RequestParam(value = "endPrice", required = false) Integer endPrice,
                            HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");

        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");

            model.addAttribute("username", username);
            model.addAttribute("name", name);
        }

        RestTemplate restTemplate = new RestTemplate();

        try {
            // GENERATE API URL
            String catalogUrl = generateUrlForAllCatalog(category, startPrice, endPrice, request);

            if (startPrice != null && endPrice != null) {
                model.addAttribute("startPrice", startPrice);
                model.addAttribute("endPrice", endPrice);
            }
            if (!category.isBlank() && !category.toLowerCase().equals("all")) {
                model.addAttribute("selectedCategory", category);
            }

            // GET LIST OF CATALOG
            ResponseEntity<ResponseAPI<List<ReadCatalogResponseDTO>>> catalogsResponse = restTemplate.exchange(
                    catalogUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });

            if (catalogsResponse.getBody() != null && catalogsResponse.getBody().getStatus() == 200) {
                List<ReadCatalogResponseDTO> catalogs = catalogsResponse.getBody().getResult();

                model.addAttribute("imageURL", setting.IMAGE_URL);
                model.addAttribute("catalogs", catalogs);
            } else {
                model.addAttribute("error", catalogsResponse.getBody().getError());
            }

            // GET LIST OF CATEGORY
            ResponseEntity<ResponseAPI<List<CategoryDTO>>> resultCategory = restTemplate.exchange(
                    setting.CATEGORY_SERVER_URL + "/all",
                    HttpMethod.GET,
                    null,
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
        return "catalog/catalog";
    }

    private String generateUrlForAllCatalog(String category, Integer startPrice, Integer endPrice, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");

        String catalogUrl = setting.CATALOG_SERVER_URL;
        String encodeCategory = category.replaceAll(" ", "-").replaceAll("&", "n").toLowerCase();

        boolean allFilter = (startPrice != null && endPrice != null) || (!encodeCategory.isBlank() && !encodeCategory.equals("all"));
        boolean priceFilterOnly = (startPrice != null && endPrice != null);

        if (jwtToken != null && !jwtToken.isBlank()) {
            var id = jwtUtils.getClaimFromJwtToken(jwtToken, "userId");
            UUID userId = UUID.fromString(id);

            catalogUrl += "/by-seller/" + userId;

            if (allFilter) {
                if (priceFilterOnly) {
                    catalogUrl += "?startPrice=" + startPrice + "&endPrice=" + endPrice;

                    if (!encodeCategory.isBlank() && !encodeCategory.equals("all")) {
                        catalogUrl += "&categoryName=" + encodeCategory;
                    }
                } else if (!encodeCategory.isBlank()) {
                    catalogUrl += "?categoryName=" + encodeCategory;
                }
            }
        } else {
            if (allFilter) {
                catalogUrl += "/filter";
                if (priceFilterOnly) {
                    catalogUrl += "?startPrice=" + startPrice + "&endPrice=" + endPrice;

                    if (!encodeCategory.isBlank() && !encodeCategory.equals("all")) {
                        catalogUrl += "&categoryName=" + encodeCategory;
                    }
                } else if (!encodeCategory.isBlank()) {
                    catalogUrl += "?categoryName=" + encodeCategory;
                }
            } else {
                // Default tanpa filter
                catalogUrl += "/all";
            }
        }
        return catalogUrl;
    }

    @GetMapping("/catalog/search")
    public String searchCatalog(@RequestParam(name = "productName", required = false) String productName, HttpServletRequest request, Model model) {
        if (productName == null || productName.isBlank()) {
            return "redirect:/catalog";
        }

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");

            model.addAttribute("username", username);
            model.addAttribute("name", name);
            headers.set("Authorization", "Bearer " + jwtToken);
        }

        HttpEntity httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseAPI<List<ReadCatalogResponseDTO>>> catalogResponse = restTemplate.exchange(
                    setting.CATALOG_SERVER_URL + "/by-name?name=" + productName,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<>() {
                    });

            if (catalogResponse.getBody() != null && catalogResponse.getBody().getStatus().equals(200)) {
                model.addAttribute("imageURL", setting.IMAGE_URL);
                model.addAttribute("catalogs", catalogResponse.getBody().getResult());
            }

            // GET LIST OF CATEGORY
            ResponseEntity<ResponseAPI<List<CategoryDTO>>> resultCategory = restTemplate.exchange(
                    setting.CATEGORY_SERVER_URL + "/all",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });

            if (resultCategory.getBody() != null && resultCategory.getBody().getStatus() == 200) {
                List<CategoryDTO> categories = resultCategory.getBody().getResult();
                model.addAttribute("categories", categories);
            } else {
                model.addAttribute("error", resultCategory.getBody().getError());
            }
            model.addAttribute("productNameSearched", productName);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "catalog/catalog";
    }

    @GetMapping("/catalog/sort")
    public String sortCatalog(@RequestParam(name = "by", required = false) String sortBy, HttpServletRequest request, Model model) {
        if (sortBy == null || sortBy.isBlank()) {
            return "redirect:/catalog";
        }

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");
        HttpHeaders headers = new HttpHeaders();

        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");

            model.addAttribute("username", username);
            model.addAttribute("name", name);
            headers.set("Authorization", "Bearer " + jwtToken);
        }

        HttpEntity httpEntity = new HttpEntity<>(headers);

        String url = switch (sortBy) {
            case "nameDesc" -> setting.CATALOG_SERVER_URL + "/sort?sortBy=name&sortOrder=desc";
            case "priceAsc" -> setting.CATALOG_SERVER_URL + "/sort?sortBy=price&sortOrder=asc";
            case "priceDesc" -> setting.CATALOG_SERVER_URL + "/sort?sortBy=price&sortOrder=desc";
            default -> setting.CATALOG_SERVER_URL + "/sort?sortBy=name&sortOrder=asc";
        };

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<ResponseAPI<List<ReadCatalogResponseDTO>>> catalogResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<>() {
                    });

            if (catalogResponse.getBody() != null && catalogResponse.getBody().getStatus().equals(200)) {
                model.addAttribute("imageURL", setting.IMAGE_URL);
                model.addAttribute("catalogs", catalogResponse.getBody().getResult());
            }

            // GET LIST OF CATEGORY
            ResponseEntity<ResponseAPI<List<CategoryDTO>>> resultCategory = restTemplate.exchange(
                    setting.CATEGORY_SERVER_URL + "/all",
                    HttpMethod.GET,
                    null,
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
        return "catalog/catalog";
    }

    @GetMapping("/catalog/{productId}")
    public String detailProduct(@PathVariable("productId") UUID productId, Model model, HttpServletRequest request)     {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseAPI<ReadCatalogResponseDTO>> catalogResponse = restTemplate.exchange(
                    setting.CATALOG_SERVER_URL + "/" + productId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            if (catalogResponse.getBody() != null && catalogResponse.getBody().getStatus().equals(200)) {
                model.addAttribute("imageURL", setting.IMAGE_URL);
                model.addAttribute("catalog", catalogResponse.getBody().getResult());
            } else {
                model.addAttribute("error", "Catalog not found, try again later");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");
        if (jwtToken != null && !jwtToken.isBlank()) {
            var username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            var name = jwtUtils.getClaimFromJwtToken(jwtToken, "name");

            model.addAttribute("username", username);
            model.addAttribute("name", name);
        }

        return "catalog/detail-catalog";
    }

    @GetMapping("/catalog/{productId}/delete-product")
    public String deleteProduct(@PathVariable("productId") UUID productId, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpSession session = request.getSession(false);
        String jwtToken = null;
        if (session != null) jwtToken = (String) session.getAttribute("token");
        if (jwtToken != null && !jwtToken.isBlank()) {
            headers.set("Authorization", "Bearer " + jwtToken);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Menghapus produk berdasarkan ID
            ResponseEntity<ResponseAPI<String>> response = restTemplate.exchange(
                    setting.CATALOG_SERVER_URL + "/" + productId + "/delete",
                    HttpMethod.DELETE,
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

        return "redirect:/catalog";
    }

}