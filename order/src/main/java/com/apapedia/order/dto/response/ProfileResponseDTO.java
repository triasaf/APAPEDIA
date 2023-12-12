package com.apapedia.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDTO {
    private UUID id;
    private String name;
    private String username;
    private String email;
    private Long balance;
    private String address;
    private Date createdAt;
    private Date updatedAt;
    private String category;
}
