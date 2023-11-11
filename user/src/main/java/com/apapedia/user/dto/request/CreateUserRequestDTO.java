package com.apapedia.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDTO {
    @NotNull
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotNull
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotNull(message = "Role cannot be null")
    private String role;
    @NotNull
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;
    @NotNull
    @NotBlank(message = "Address cannot be blank")
    private String address;
}
