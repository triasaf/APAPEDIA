package com.apapedia.order.restService;

import com.apapedia.order.dto.request.UpdateOrderRequestDTO;
import com.apapedia.order.dto.response.SalesDTO;
import com.apapedia.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRestService {
    List<Order> findOrderByCustomerId(UUID customer);
    List<Order> findOrderBySellerId(UUID seller);

    List<Order> getAllOrder();
    Order findOrderById(UUID orderId);

    Order createRestOrder(Order order);

    List<SalesDTO> getDailySalesBySellerId(UUID sellerId);

<<<<<<< HEAD
    Order changeStatusOrder(UpdateOrderRequestDTO orderDTO);
=======
    void createOrderRequestDTO(UUID customerId);
>>>>>>> b47d3e8c80ab8bd5e4d9f7ae60b6cf6f8bcb2208
}
