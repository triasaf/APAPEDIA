package com.apapedia.catalog.restService;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;

import java.util.List;
import java.util.UUID;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;

public interface CatalogRestService {

    public List<Catalog> getAllCatalogsSortedByName();

    public Catalog getCatalogById(UUID idCatalog);

    public Catalog createCatalog(CreateCatalogRequestDTO createCatalogRequestDTO);

    public List<Catalog> getCatalogsBySellerId(UUID idSeller);

    public Catalog updateCatalog(UUID idCatalog, UpdateCatalogRequestDTO CatalogDTO);

    public List<Catalog> getCatalogListByPriceRange(Integer startPrice, Integer endPrice);
}
