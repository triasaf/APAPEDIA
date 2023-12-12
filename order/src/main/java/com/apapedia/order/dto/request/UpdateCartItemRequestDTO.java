package com.apapedia.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import jakarta.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCartItemRequestDTO {
    private UUID id;
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}



