package com.apapedia.frontend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequestDTO {
    @NotNull
    private UUID userId;
    @NotNull
    @NotBlank
    private String method;
    @NotNull
    private Integer amount;
}
