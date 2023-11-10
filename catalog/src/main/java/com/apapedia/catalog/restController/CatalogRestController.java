package com.apapedia.catalog.restController;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.dto.response.ResponseAPI;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restService.CatalogRestService;

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
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }

        return response;
    }

}
