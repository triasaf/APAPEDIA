package com.apapedia.order.dto.request;

import com.apapedia.order.model.Cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCartItemRequestDTO {
    @NotNull(message = "Product Id must not be null")
    private UUID productId;
    @NotNull(message = "Cart Id must not be null")
    private Cart cartId;
    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
