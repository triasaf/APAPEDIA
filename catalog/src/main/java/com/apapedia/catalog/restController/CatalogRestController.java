package com.apapedia.catalog.restController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
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
        }

        return response;
    }

    @GetMapping("/filter")
    public ResponseAPI getCatalogListByFilter(
            @RequestParam(name = "startPrice", required = false) Integer startPrice,
            @RequestParam(name = "endPrice", required = false) Integer endPrice,
            @RequestParam(name = "categoryName", required = false) String categoryName) {

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
    public ResponseAPI getCatalogListByName(@RequestParam(value = "name") String catalogName) {
        var response = new ResponseAPI<>();

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(catalogRestService.getCatalogListByProductName(catalogName));
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
            @RequestParam(required = false, defaultValue = "asc", name = "sortOrder") String sortOrder) {

        var response = new ResponseAPI<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.name());
        response.setResult(catalogRestService.getSortedCatalog(sortBy, sortOrder));

        return response;
    }

}
