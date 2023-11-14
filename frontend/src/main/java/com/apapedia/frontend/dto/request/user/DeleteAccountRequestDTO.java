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
public class DeleteAccountRequestDTO {
    @NotNull(message = "User ID must not be null")
    public UUID userId;
    @NotNull(message = "Password must not be null")
    @NotBlank(message = "Password must not be blank")
    public String password;
}
