package com.apapedia.catalog.restController;

import com.apapedia.catalog.dto.response.ResponseAPI;
import com.apapedia.catalog.restService.CategoryRestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/category")
public class CategoryRestController {
    @Autowired
    private CategoryRestService categoryRestService;

    @GetMapping("/all")
    public ResponseAPI getAllCategory() {
        var response = new ResponseAPI<>();

        response.setStatus(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.name());
        response.setResult(categoryRestService.getAllCategories());

        return response;
    }

    @GetMapping("/{slug}")
    public ResponseAPI getCatalogByCategory(@PathVariable(value = "slug") String slug) {
        var response = new ResponseAPI<>();
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(categoryRestService.getCatalogByCategory(slug));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setResult(e.getMessage());
        }

        return response;
    }
}
