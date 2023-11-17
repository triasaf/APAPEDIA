package com.apapedia.catalog.restService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.repository.CatalogDb;
import com.apapedia.catalog.dto.CatalogMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Catalog> getCatalogListByProductName(String productName) {
        List<Catalog> searchedCatalog = catalogDb.findAllByProductNameContainingIgnoreCaseOrderByProductName(productName);
        if (searchedCatalog.isEmpty()) {
            throw new NoSuchElementException("Product not found");
        }
        return searchedCatalog;
    }
}
