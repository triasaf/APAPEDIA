package com.apapedia.frontend.dto.response;

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
    private String productName;
    private Integer price;
    private String productDescription;
    private Integer stock;
    private String image;
    private CategoryDTO categoryId;
}