package com.apapedia.frontend.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private UUID id;
    private Date createdAt;
    private Date updatedAt;
    private Integer status;
    private Integer totalPrice;
    private UUID customer;
    private UUID seller;
    private List<OrderItemResponseDTO> listOrderItem;
}
