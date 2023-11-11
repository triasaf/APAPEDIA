package com.apapedia.catalog.restController;

import java.util.NoSuchElementException;
import java.util.UUID;
import com.apapedia.catalog.dto.response.ResponseAPI;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restService.CatalogRestService;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogRestController {
    @Autowired
    private CatalogRestService catalogRestService;

    @GetMapping("/all")
    public ResponseAPI getAllCatalogsSorted() {
        var response = new ResponseAPI<>();

        response.setStatus(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.name());
        response.setResult(catalogRestService.getAllCatalogsSortedByName());

        return response;
    }

    @GetMapping("/{id}")
    public ResponseAPI getCatalogById(@PathVariable(value = "id") UUID id) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getCatalogById(id));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    @PostMapping("/add")
    public ResponseAPI addCatalog(@RequestBody CreateCatalogRequestDTO createCatalogRequestDTO) {

        Catalog catalog = catalogRestService.createCatalog(createCatalogRequestDTO);

        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalog);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    @GetMapping("/by-seller/{id}")
    public ResponseAPI getCatalogBySellerId(@PathVariable(value = "id") UUID idSeller) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getCatalogsBySellerId(idSeller));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @PutMapping(value = "/update/{id}")
    public ResponseAPI updateCatalog(@PathVariable(value = "id") UUID id,
            @RequestBody UpdateCatalogRequestDTO catalogDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<>();

        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                res.append(error.getDefaultMessage()).append(" ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
        }

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.updateCatalog(id, catalogDTO));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }

        return response;
    }

    @GetMapping("/filter")
    public ResponseAPI getCatalogListByPriceRange(@RequestParam(name = "startPrice") Integer startPrice,
            @RequestParam(name = "endPrice") Integer endPrice) {
        var response = new ResponseAPI<>();

        try {
            List<Catalog> filteredCatalogList = catalogRestService.getCatalogListByPriceRange(startPrice, endPrice);

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

}
