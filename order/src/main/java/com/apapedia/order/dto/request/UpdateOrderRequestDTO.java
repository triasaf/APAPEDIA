package com.apapedia.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateOrderRequestDTO {
    private UUID id;

    @Min(value = 0, message = "Status must be between (inclusive) 0-5")
    @Max(value = 5, message = "Status must be between (inclusive) 0-5")
    private Integer status;

}



