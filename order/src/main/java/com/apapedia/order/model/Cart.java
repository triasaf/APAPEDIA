package com.apapedia.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "total_price", nullable = false)
    private Integer totalPrice = 0;

    @OneToMany(mappedBy = "cartId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> listCartItem = new ArrayList<>();
}
