package com.apapedia.catalog.restService;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;

import java.util.List;
import java.util.UUID;

public interface CatalogRestService {

    public List<Catalog> getAllCatalogsSortedByName();

    public Catalog getCatalogById(UUID idCatalog);

    public Catalog createCatalog(Catalog catalog);

    public List<Catalog> getCatalogsBySellerId(UUID idSeller);

    public Catalog updateCatalog(UpdateCatalogRequestDTO CatalogDTO);

    public List<Catalog> getCatalogListByPriceRange(Integer startPrice, Integer endPrice);
}
