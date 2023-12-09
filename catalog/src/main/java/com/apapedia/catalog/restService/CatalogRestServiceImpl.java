package com.apapedia.catalog.restService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.dto.response.ResponseAPI;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.repository.CatalogDb;
import com.apapedia.catalog.dto.CatalogMapper;

import com.apapedia.catalog.setting.Setting;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CatalogRestServiceImpl implements CatalogRestService {
    @Autowired
    private CatalogDb catalogDb;
    @Autowired
    private CatalogMapper catalogMapper;
    @Autowired
    private CategoryRestService categoryRestService;
    @Autowired
    private Setting setting;

    @Override
    @Transactional
    public List<Catalog> getAllCatalogsSortedByName() {
        return catalogDb.findAllByIsDeletedFalseOrderByProductName();
    }

    @Override
    public Catalog getCatalogById(UUID idCatalog) {
        var catalog = catalogDb.findById(idCatalog);
        if (catalog.isPresent() && !catalog.get().isDeleted())
            return catalog.get();
        else
            throw new NoSuchElementException("Catalog not found");
    }

    @Override
    public Catalog createCatalog(Catalog catalog) {
        return catalogDb.save(catalog);
    }

    @Override
    @Transactional
    public List<Catalog> getCatalogsBySellerId(UUID idSeller, Integer startPrice, Integer endPrice, String categoryName) {
        RestTemplate restTemplate = new RestTemplate();
        List<Catalog> catalogs = catalogDb.findAllBySellerAndIsDeletedFalseOrderByProductName(idSeller);

        ResponseEntity<ResponseAPI<Boolean>> response = restTemplate.exchange(
                setting.USER_SERVER_URL + "/user/is-exist/" + idSeller,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ResponseAPI<Boolean>>() {
                });
        if (response.getBody() != null && response.getBody().getStatus().equals(200)) {
            if (!response.getBody().getResult()) {
                throw new NoSuchElementException("Seller does not exist");
            }
        } else {
            throw new RestClientException(response.getBody().getError());
        }

        if (startPrice != null && endPrice != null && categoryName != null && !categoryName.isEmpty()) {
            // Jika ada filter nama category dan filter harga
            List<Catalog> existingCatalogCategory = categoryRestService.getCatalogByCategory(categoryName);
            List<Catalog> existingCatalogPrice = catalogDb.findAllBySellerAndIsDeletedFalseAndPriceBetweenOrderByProductName(idSeller, startPrice, endPrice);
            existingCatalogCategory.retainAll(existingCatalogPrice);
            catalogs = existingCatalogCategory;
        } else if (categoryName != null && !categoryName.isEmpty()) {
            // Jika hanya filter category
            catalogs.retainAll(categoryRestService.getCatalogByCategory(categoryName));
        } else if (startPrice != null && endPrice != null) {
            // jika hanya filter harga
            catalogs = catalogDb.findAllBySellerAndIsDeletedFalseAndPriceBetweenOrderByProductName(idSeller, startPrice, endPrice);
        }

        return catalogs;
    }

    @Override
    public Catalog updateCatalog(UpdateCatalogRequestDTO catalogDTO) {
        Catalog existingCatalog = getCatalogById(catalogDTO.getId());

        Catalog updatedCatalog = catalogMapper.updateCatalogRequestDTOToCatalog(catalogDTO);

        existingCatalog.setProductName(updatedCatalog.getProductName());
        existingCatalog.setPrice(updatedCatalog.getPrice());
        existingCatalog.setProductDescription(updatedCatalog.getProductDescription());
        existingCatalog.setStok(updatedCatalog.getStok());
        existingCatalog.setCategoryId(updatedCatalog.getCategoryId());

        if (catalogDTO.getImage() != null) {
            existingCatalog.setImage(catalogDTO.getImage());
        }

        return catalogDb.save(existingCatalog);
    }

    @Override
    @Transactional
    public List<Catalog> getCatalogListByPriceRange(Integer startPrice, Integer endPrice) {
        List<Catalog> existingCatalog = catalogDb.findByPriceBetweenAndIsDeletedFalse(startPrice, endPrice);
        if (existingCatalog.isEmpty()) {
            throw new NoSuchElementException("Catalog not found");
        }
        return existingCatalog;
    }

    @Override
    @Transactional
    public List<Catalog> getCatalogListByFilter(Integer startPrice, Integer endPrice, String categoryName) {
        List<Catalog> existingCatalog;

        if (startPrice != null && endPrice != null && categoryName != null && !categoryName.isEmpty()) {
            // Jika ada filter nama category dan filter harga
            List<Catalog> existingCatalogCategory = categoryRestService.getCatalogByCategory(categoryName);
            List<Catalog> existingCatalogPrice = catalogDb.findByPriceBetweenAndIsDeletedFalse(startPrice, endPrice);
            existingCatalogCategory.retainAll(existingCatalogPrice);
            existingCatalog = existingCatalogCategory;
        } else if (categoryName != null && !categoryName.isEmpty()) {
            // Jika hanya filter category
            existingCatalog = categoryRestService.getCatalogByCategory(categoryName);
        } else if (startPrice != null && endPrice != null) {
            // jika hanya filter harga
            existingCatalog = catalogDb.findByPriceBetweenAndIsDeletedFalse(startPrice, endPrice);
        } else {
            throw new IllegalArgumentException("Invalid filter parameters");
        }

        if (existingCatalog.isEmpty()) {
            throw new NoSuchElementException("Catalog not found");
        }

        return existingCatalog;
    }

    @Override
    @Transactional
    public List<Catalog> getCatalogListByProductName(String productName) {
        List<Catalog> searchedCatalog = catalogDb
                .findAllByProductNameContainingIgnoreCaseAndIsDeletedFalseOrderByProductName(productName);
        if (searchedCatalog.isEmpty()) {
            throw new NoSuchElementException("Product not found");
        }
        return searchedCatalog;
    }

    @Override
    @Transactional
    public List<Catalog> getSortedCatalog(String sortBy, String sortOrder) {
        List<Catalog> catalogList;

        if ("name".equalsIgnoreCase(sortBy)) {
            catalogList = "asc".equalsIgnoreCase(sortOrder) ? catalogDb.findAllByIsDeletedFalseOrderByProductNameAsc()
                    : catalogDb.findAllByIsDeletedFalseOrderByProductNameDesc();
        } else if ("price".equalsIgnoreCase(sortBy)) {
            catalogList = "asc".equalsIgnoreCase(sortOrder) ? catalogDb.findAllByIsDeletedFalseOrderByPriceAsc()
                    : catalogDb.findAllByIsDeletedFalseOrderByPriceDesc();
        } else {
            // Handle other cases or provide a default sorting option
            catalogList = catalogDb.findAll();
        }

        return catalogList;
    }

    @Override
    public void deleteCatalog(UUID id) {
        Catalog catalog = getCatalogById(id);
        catalog.setDeleted(true);
        catalogDb.save(catalog);
    }
}
