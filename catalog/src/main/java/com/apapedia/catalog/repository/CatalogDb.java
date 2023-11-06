package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CatalogDb extends JpaRepository<Catalog, UUID> {
}
