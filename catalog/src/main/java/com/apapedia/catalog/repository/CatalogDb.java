package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CatalogDb extends JpaRepository<Catalog, UUID> {

    List<Catalog> findAllByOrderByProductName();
    List<Catalog> findAllBySellerOrderByProductName(UUID seller);
}
