package com.apapedia.frontend.dto.request.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private Integer quantity;
    private String productName;
    private Integer productPrice;
}
