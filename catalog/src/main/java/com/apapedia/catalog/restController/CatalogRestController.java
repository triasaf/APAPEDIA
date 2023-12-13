package com.apapedia.catalog.restController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.apapedia.catalog.dto.request.SubstractCatalogStockDTO;
import com.apapedia.catalog.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.apapedia.catalog.dto.CatalogMapper;
import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.dto.response.ResponseAPI;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restService.CatalogRestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalog")
public class CatalogRestController {
    @Autowired
    private CatalogRestService catalogRestService;
    @Autowired
    private CatalogMapper catalogMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/all")
    public ResponseAPI getAllCatalogsSorted() {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getAllCatalogsSortedByName());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setResult(e.getMessage());
        }
        return response;
    }

    @GetMapping(value = "/image/{catalogId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource getImage(@PathVariable(value = "catalogId") UUID catalogId) {
        var catalog = catalogRestService.getCatalogById(catalogId);
        return new ByteArrayResource(catalog.getImage());
    }

    @GetMapping("/{id}")
    public ResponseAPI getCatalogById(@PathVariable(value = "id") UUID id) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getCatalogById(id));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setResult(e.getMessage());
        }

        return response;
    }

    @PostMapping("/add")
    public ResponseAPI addCatalog(@Valid @RequestBody CreateCatalogRequestDTO createCatalogRequestDTO,
            BindingResult bindingResult) {
        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() - 1)
                    res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            var catalog = catalogMapper.createCatalogRequestDTOToCatalog(createCatalogRequestDTO);
            var newCatalog = catalogRestService.createCatalog(catalog);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(newCatalog);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    @GetMapping("/by-seller/{id}")
    public ResponseAPI getCatalogBySellerId(
            @PathVariable(value = "id") UUID idSeller,
            @RequestParam(name = "startPrice", required = false) Integer startPrice,
            @RequestParam(name = "endPrice", required = false) Integer endPrice,
            @RequestParam(name = "categoryName", required = false) String categoryName
    ) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getCatalogsBySellerId(idSeller, startPrice, endPrice, categoryName));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @PutMapping(value = "/update")
    public ResponseAPI updateCatalog(@Valid @RequestBody UpdateCatalogRequestDTO catalogDTO,
            BindingResult bindingResult) {
        var response = new ResponseAPI<>();

        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() - 1)
                    res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.updateCatalog(catalogDTO));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setResult(e.getMessage());
        }
        return response;
    }

    @GetMapping("/filter")
    public ResponseAPI getCatalogListByFilter(
            @RequestParam(name = "startPrice", required = false) Integer startPrice,
            @RequestParam(name = "endPrice", required = false) Integer endPrice,
            @RequestParam(name = "categoryName", required = false) String categoryName
    ) {
        var response = new ResponseAPI<>();

        try {
            List<Catalog> filteredCatalogList = catalogRestService.getCatalogListByFilter(startPrice, endPrice,
                    categoryName);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(filteredCatalogList);
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @GetMapping("/by-name")
    public ResponseAPI getCatalogListByName(@RequestParam(value = "name") String catalogName, HttpServletRequest request) {
        var response = new ResponseAPI<>();

        UUID sellerId = null;
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            sellerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getCatalogListByProductName(catalogName, sellerId));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @GetMapping("/sort")
    public ResponseAPI getSortedCatalog(
            @RequestParam(required = false, defaultValue = "name", name = "sortBy") String sortBy,
            @RequestParam(required = false, defaultValue = "asc", name = "sortOrder") String sortOrder,
            HttpServletRequest request) {

        UUID sellerId = null;
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            sellerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }

        var response = new ResponseAPI<>();
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getSortedCatalog(sortBy, sortOrder, sellerId));
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/{id}/delete")
    public ResponseAPI deleteCatalog(@PathVariable(value = "id") UUID id, HttpServletRequest request) {
        UUID sellerId = null;
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            sellerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }

        var response = new ResponseAPI<>();
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            catalogRestService.deleteCatalog(id, sellerId);
            response.setResult("Product has been deleted.");
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @PutMapping("/substract-stock")
    public ResponseAPI<String> subsctractCatalogStock(@Valid @RequestBody SubstractCatalogStockDTO stockDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<String>();

        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() - 1)
                    res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            catalogRestService.substractCatalogStock(stockDTO);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult("Catalog stock updated successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setError(e.getMessage());
        }
        return response;
    }

}
