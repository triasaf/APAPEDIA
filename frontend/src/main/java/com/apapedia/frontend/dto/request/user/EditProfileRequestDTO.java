package com.apapedia.frontend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditProfileRequestDTO {
    private UUID userId;
    private String updatedAttribute;
    private String newValue;
}
