package com.apapedia.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubstractCatalogStockDTO {
    @NotNull(message = "Catalog ID must not be null")
    private UUID catalogId;
    @NotNull(message = "Stock reduced must not be null")
    @Positive(message = "Stock reduced must be positive")
    private int stockReduced;
}
