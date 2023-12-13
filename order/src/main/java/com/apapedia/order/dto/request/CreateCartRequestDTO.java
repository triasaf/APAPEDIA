package com.apapedia.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCartRequestDTO {
    @NotNull
    private UUID id;

    @NotNull
    private UUID userId;
}
