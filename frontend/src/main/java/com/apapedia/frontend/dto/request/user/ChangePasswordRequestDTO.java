package com.apapedia.frontend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDTO {
    private UUID userId;
    private String oldPassword;
    private String newPassword;
    private String newPassword2;
}