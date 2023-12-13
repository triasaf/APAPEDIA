package com.apapedia.catalog.dto.request;

import java.util.UUID;

import com.apapedia.catalog.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCatalogRequestDTO {

    @NotNull(message = "Seller is required")
    private UUID seller; // TODO: seller logged in

    @Positive(message = "Price must be positive")
    @NotNull(message = "Price is required")
    private Integer price;

    @NotNull(message = "Product name cannot be null")
    @NotBlank(message = "Name is required")
    private String productName;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Deacription is required")
    private String productDescription;

    @NotNull(message = "Category is required")
    private Category categoryId;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stok must be positive")
    private Integer stok;

    private byte[] image;
}
