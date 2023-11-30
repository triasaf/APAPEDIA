package com.apapedia.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCartItemRequestDTO {
    private UUID id;
    private Integer quantity;

}



