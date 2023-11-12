package com.apapedia.user.dto.request;

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
}
