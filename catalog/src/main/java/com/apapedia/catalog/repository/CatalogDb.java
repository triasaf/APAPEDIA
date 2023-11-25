package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CatalogDb extends JpaRepository<Catalog, UUID> {

    // List<Catalog> findAllByOrderByProductName();
    // List<Catalog> findAllBySellerOrderByProductName(UUID seller);
    // List<Catalog> findByPriceBetween(Integer startPrice, Integer endPrice);
    // List<Catalog> findAllByProductNameContainingIgnoreCaseOrderByProductName(String productName);
    // List<Catalog> findAllByOrderByProductNameAsc();
    // List<Catalog> findAllByOrderByProductNameDesc();
    // List<Catalog> findAllByOrderByPriceAsc();
    // List<Catalog> findAllByOrderByPriceDesc();

    List<Catalog> findAllByIsDeletedFalseOrderByProductName();
    List<Catalog> findAllBySellerAndIsDeletedFalseOrderByProductName(UUID seller);
    List<Catalog> findByPriceBetweenAndIsDeletedFalse(Integer startPrice, Integer endPrice);
    List<Catalog> findAllByProductNameContainingIgnoreCaseAndIsDeletedFalseOrderByProductName(String productName);
    List<Catalog> findAllByIsDeletedFalseOrderByProductNameAsc();
    List<Catalog> findAllByIsDeletedFalseOrderByProductNameDesc();
    List<Catalog> findAllByIsDeletedFalseOrderByPriceAsc();
    List<Catalog> findAllByIsDeletedFalseOrderByPriceDesc();
    
}
