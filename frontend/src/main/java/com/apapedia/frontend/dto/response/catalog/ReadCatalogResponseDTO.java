package com.apapedia.frontend.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.apapedia.frontend.dto.request.catalog.CategoryDTO;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadCatalogResponseDTO {
    private UUID id;
    private UUID seller;
    private String productName;
    private Integer price;
    private String productDescription;
    private Integer stok;
    private CategoryDTO categoryId;
}