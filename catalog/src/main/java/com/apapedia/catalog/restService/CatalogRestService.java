package com.apapedia.catalog.restService;

import java.util.List;
import java.util.UUID;

import com.apapedia.catalog.dto.request.CreateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;

public interface CatalogRestService {

    public List<Catalog> getAllCatalogsSortedByName();

    public Catalog getCatalogById(UUID idCatalog);

    public Catalog createCatalog(CreateCatalogRequestDTO createCatalogRequestDTO);

    public List<Catalog> getCatalogsBySellerId(UUID idSeller);

}
