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
public class ChangePasswordRequestDTO {
    @NotNull
    private UUID userId;
    @NotNull
    @NotBlank
    private String oldPassword;
    @NotNull
    @NotBlank
    private String newPassword;
    @NotNull
    @NotBlank
    private String newPassword2;
}