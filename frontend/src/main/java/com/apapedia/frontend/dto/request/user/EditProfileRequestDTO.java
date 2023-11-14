package com.apapedia.frontend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditProfileRequestDTO {
    @NotNull
    private UUID userId;
    @NotNull
    @NotBlank
    private String updatedAttribute;
    @NotNull
    @NotBlank
    private String newValue;
}
