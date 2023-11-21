package com.apapedia.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogDTO {
    private UUID id = UUID.randomUUID();
    private UUID seller;
    private Integer price;
    private String productName;
    private String productDescription;
    private CategoryDTO categoryId;
    private Integer stok;
    private String image;
}
