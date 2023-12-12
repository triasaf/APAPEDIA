package com.apapedia.order.dto.helper;

import com.apapedia.order.dto.request.CatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogQuantityPairDTO {
    CatalogDTO catalog;
    int quantity;
}
