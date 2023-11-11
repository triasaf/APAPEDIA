package com.apapedia.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.apapedia.catalog.model.Category;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCatalogRequestDTO {
    @NotNull
    @NotBlank(message = "Product Name is required")
    private String productName;

    @NotNull
    @NotBlank(message = "Price is required")
    @Positive
    private Integer price;

    @NotNull
    @NotBlank(message = "Product Description is required")
    private String productDescription;

    @NotNull
    @NotBlank(message = "Stok is required")
    @Positive
    private Integer stok;

    @NotNull
    @NotBlank(message = "Image is required")
    private String image;

    @NotNull
    @NotBlank(message = "Category is required")
    private Category categoryId;

}
