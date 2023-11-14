package com.apapedia.frontend.dto.request.catalog;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCatalogRequestDTO {

    private UUID seller; // TODO: seller logged in

    @NotBlank(message = "Price is required")
    private Integer price;

    @NotBlank(message = "Name is required")
    private String productName;

    @NotBlank(message = "Deacription is required")
    private String productDescription;

    @NotBlank(message = "Category is required")
    private UUID categoryId;

    @NotBlank(message = "Stock is required")
    private Integer stok;

    @NotBlank(message = "Image is required")
    private String image;
}
