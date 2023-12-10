package com.apapedia.catalog.repository;

import com.apapedia.catalog.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CatalogDb extends JpaRepository<Catalog, UUID> {
    List<Catalog> findAllByIsDeletedFalseOrderByProductName();
    List<Catalog> findAllBySellerAndIsDeletedFalseOrderByProductName(UUID seller);
    List<Catalog> findAllBySellerAndIsDeletedFalseAndPriceBetweenOrderByProductName(UUID seller, Integer startPrice, Integer endPrice);
    List<Catalog> findByPriceBetweenAndIsDeletedFalse(Integer startPrice, Integer endPrice);
    List<Catalog> findAllByProductNameContainingIgnoreCaseAndIsDeletedFalseOrderByProductName(String productName);
    List<Catalog> findAllByIsDeletedFalseOrderByProductNameAsc();
    List<Catalog> findAllByIsDeletedFalseOrderByProductNameDesc();
    List<Catalog> findAllByIsDeletedFalseOrderByPriceAsc();
    List<Catalog> findAllByIsDeletedFalseOrderByPriceDesc();
    List<Catalog> findAllByProductNameContainingIgnoreCaseAndSellerAndIsDeletedFalseOrderByProductName(String productName, UUID sellerId);
}
