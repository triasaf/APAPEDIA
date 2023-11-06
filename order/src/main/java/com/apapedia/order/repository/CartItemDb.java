package com.apapedia.order.repository;

import com.apapedia.order.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemDb extends JpaRepository<CartItem, UUID> {
}
