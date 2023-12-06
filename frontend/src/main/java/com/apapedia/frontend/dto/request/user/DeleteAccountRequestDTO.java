package com.apapedia.frontend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteAccountRequestDTO {
    public UUID userId;
    public String password;
}
