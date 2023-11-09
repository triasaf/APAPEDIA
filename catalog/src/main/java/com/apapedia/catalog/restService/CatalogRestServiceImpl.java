package com.apapedia.catalog.restService;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.repository.CatalogDb;
import com.apapedia.catalog.repository.CategoryDb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CatalogRestServiceImpl implements CatalogRestService {
    @Autowired
    private CatalogDb catalogDb;

    @Autowired
    private CategoryDb categoryDb;

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
}
