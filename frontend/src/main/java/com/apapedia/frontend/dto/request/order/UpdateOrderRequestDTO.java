package com.apapedia.frontend.dto.request.order;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequestDTO {
    private UUID id;
    private Integer status;
}
