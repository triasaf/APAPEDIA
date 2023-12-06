package com.apapedia.frontend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserRequestDTO {
    private String role;
    private String name;
    private String username;
    private String password;
    private String email;
    private String address;
    private String category;
}
