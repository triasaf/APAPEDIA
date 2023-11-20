package com.apapedia.frontend.dto.request.order;

import java.util.UUID;

public class OrderItemResponseDTO {
    private UUID id;
    private UUID productId;
    private Integer quantity;
    private String productName;
    private Integer productPrice;
}
