package com.apapedia.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotNull
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotNull
    @NotBlank(message = "Password must not be blank")
    private String password;
}
