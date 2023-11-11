package com.apapedia.catalog.dto;

import com.apapedia.catalog.model.Catalog;

import org.mapstruct.Mapper;

import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    Catalog updateCatalogRequestDTOToCatalog(UpdateCatalogRequestDTO updateCatalogRequestDTO);

    UpdateCatalogRequestDTO CatalogToUpdateCatalogRequestDTO(Catalog catalog);

}
