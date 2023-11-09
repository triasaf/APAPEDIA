package com.apapedia.catalog.restService;

import com.apapedia.catalog.model.Catalog;

import java.util.List;

import java.util.UUID;

public interface CatalogRestService {

    public List<Catalog> getAllCatalogsSortedByName();

    public Catalog getCatalogById(UUID idCatalog);
}
