package com.apapedia.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCatalogRequestDTO extends CreateCatalogRequestDTO{
    @NotNull(message = "Id must not be null")
    private UUID id;
}
