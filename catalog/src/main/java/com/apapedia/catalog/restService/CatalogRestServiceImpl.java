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

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.repository.CategoryDb;

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
    public Catalog createCatalog(CreateCatalogRequestDTO createCatalogRequestDTO) {
        Catalog newCatalog = new Catalog();
        Category category = categoryRestService.getCategoryById(createCatalogRequestDTO.getCategoryId());

        newCatalog.setSeller(createCatalogRequestDTO.getSeller());
        newCatalog.setProductName(createCatalogRequestDTO.getProductName());
        newCatalog.setPrice(createCatalogRequestDTO.getPrice());
        newCatalog.setProductDescription(createCatalogRequestDTO.getProductDescription());
        newCatalog.setStok(createCatalogRequestDTO.getStok());
        newCatalog.setImage(createCatalogRequestDTO.getImage());
        newCatalog.setCategoryId(category);

        catalogDb.save(newCatalog);

        return newCatalog;
    }

    @Override
    public List<Catalog> getCatalogsBySellerId(UUID idSeller) {
        return catalogDb.findAllBySellerOrderByProductName(idSeller);
    }

    public Catalog updateCatalog(UUID idCatalog, UpdateCatalogRequestDTO CatalogDTO) {
        Catalog existingCatalog = getCatalogById(idCatalog);
        Catalog updatedCatalog = catalogMapper.updateCatalogRequestDTOToCatalog(CatalogDTO);

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
}
