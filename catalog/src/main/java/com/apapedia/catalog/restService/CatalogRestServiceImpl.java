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

    @Override
    public List<Catalog> getAllCatalogsSortedByName() {
        return catalogDb.findAllByOrderByProductName();
    }

    @Override
    public Catalog getCatalogById(UUID idCatalog) {
        var catalog = catalogDb.findById(idCatalog);
        if (catalog.isPresent())
            return catalog.get();
        else
            throw new NoSuchElementException("Catalog not found");
    }

    @Override
    public Catalog createCatalog(Catalog catalog) {
        return catalogDb.save(catalog);
    }

    @Override
    public List<Catalog> getCatalogsBySellerId(UUID idSeller) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ResponseAPI<Boolean>> response = restTemplate.exchange(
                Setting.USER_SERVER_URL + "/user/is-exist/" + idSeller,
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

        return catalogDb.findAllBySellerOrderByProductName(idSeller);
    }

    @Override
    public Catalog updateCatalog(UpdateCatalogRequestDTO catalogDTO) {
        Catalog existingCatalog = getCatalogById(catalogDTO.getId());

        Catalog updatedCatalog = catalogMapper.updateCatalogRequestDTOToCatalog(catalogDTO);

        existingCatalog.setProductName(updatedCatalog.getProductName());
        existingCatalog.setPrice(updatedCatalog.getPrice());
        existingCatalog.setProductDescription(updatedCatalog.getProductDescription());
        existingCatalog.setStok(updatedCatalog.getStok());
        existingCatalog.setImage(updatedCatalog.getImage());
        existingCatalog.setCategoryId(updatedCatalog.getCategoryId());

        return catalogDb.save(existingCatalog);
    }

    @Override
    public List<Catalog> getCatalogListByPriceRange(Integer startPrice, Integer endPrice) {
        List<Catalog> existingCatalog = catalogDb.findByPriceBetween(startPrice, endPrice);
        if (existingCatalog.isEmpty()) {
            throw new NoSuchElementException("Catalog not found");
        }
        return existingCatalog;
    }

    @Override
    public List<Catalog> getCatalogListByFilter(Integer startPrice, Integer endPrice, String categoryName) {
        List<Catalog> existingCatalog;

        if (startPrice != null && endPrice != null && categoryName != null && !categoryName.isEmpty()) {
            // Jika ada filter nama category dan filter harga
            List<Catalog> existingCatalogCategory = categoryRestService.getCatalogByCategory(categoryName);
            List<Catalog> existingCatalogPrice = catalogDb.findByPriceBetween(startPrice, endPrice);
            existingCatalogCategory.retainAll(existingCatalogPrice);
            existingCatalog = existingCatalogCategory;
        } else if (categoryName != null && !categoryName.isEmpty()) {
            // Jika hanya filter category
            existingCatalog = categoryRestService.getCatalogByCategory(categoryName);
        } else if (startPrice != null && endPrice != null) {
            // jika hanya filter harga
            existingCatalog = catalogDb.findByPriceBetween(startPrice, endPrice);
        } else {
            throw new IllegalArgumentException("Invalid filter parameters");
        }

        if (existingCatalog.isEmpty()) {
            throw new NoSuchElementException("Catalog not found");
        }

        return existingCatalog;
    }

    @Override
    public List<Catalog> getCatalogListByProductName(String productName) {
        List<Catalog> searchedCatalog = catalogDb
                .findAllByProductNameContainingIgnoreCaseOrderByProductName(productName);
        if (searchedCatalog.isEmpty()) {
            throw new NoSuchElementException("Product not found");
        }
        return searchedCatalog;
    }

    @Override
    public List<Catalog> getSortedCatalog(String sortBy, String sortOrder) {
        List<Catalog> catalogList;

        if ("name".equalsIgnoreCase(sortBy)) {
            catalogList = "asc".equalsIgnoreCase(sortOrder) ? catalogDb.findAllByOrderByProductNameAsc()
                    : catalogDb.findAllByOrderByProductNameDesc();
        } else if ("price".equalsIgnoreCase(sortBy)) {
            catalogList = "asc".equalsIgnoreCase(sortOrder) ? catalogDb.findAllByOrderByPriceAsc()
                    : catalogDb.findAllByOrderByPriceDesc();
        } else {
            // Handle other cases or provide a default sorting option
            catalogList = catalogDb.findAll();
        }

        return catalogList;
    }
}
