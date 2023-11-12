package com.apapedia.order.dto.request;

import com.apapedia.order.model.Cart;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @NotNull
    private UUID productId;
    @NotNull
    private Cart cartId;
    @NotNull
    @Positive
    private Integer quantity;


}
