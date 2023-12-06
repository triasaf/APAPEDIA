package com.apapedia.frontend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequestDTO {
    private UUID userId;
    private String method;
    private Integer amount;
}
