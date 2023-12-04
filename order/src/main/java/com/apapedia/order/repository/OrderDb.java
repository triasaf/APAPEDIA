package com.apapedia.order.repository;

import com.apapedia.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface OrderDb extends JpaRepository<Order, UUID> {

    // @Query("SELECT DATE(o.createdAt) AS date, SUM(oi.quantity) AS
    // numberOfProductsSold " +
    // "FROM Order o " +
    // "JOIN o.listOrderItem oi " +
    // "WHERE o.seller = :sellerId " +
    // "GROUP BY DATE(o.createdAt)")
    // List<Object[]> getDailySalesDataBySellerId(@Param("sellerId") UUID sellerId);

    @Query("SELECT DAY(o.createdAt) AS day, SUM(oi.quantity) AS numberOfProductsSold " +
            "FROM Order o " +
            "JOIN o.listOrderItem oi " +
            "WHERE o.seller = :sellerId " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
            "GROUP BY DAY(o.createdAt)")
    List<Object[]> getDailySalesDataBySellerId(@Param("sellerId") UUID sellerId);

}
