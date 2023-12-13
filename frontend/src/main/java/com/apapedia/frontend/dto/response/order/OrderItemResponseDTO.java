package com.apapedia.frontend.dto.response.order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private UUID id;
    private UUID productId;
    private Integer quantity;
    private String productName;
    private Integer productPrice;
}
