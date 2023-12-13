package com.apapedia.order.restService;

import com.apapedia.order.dto.request.UpdateOrderRequestDTO;
import com.apapedia.order.dto.response.SalesDTO;
import com.apapedia.order.model.Order;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface OrderRestService {
    List<Order> findOrderByCustomerId(UUID customer);

    List<Order> findOrderBySellerId(UUID seller);

    List<Order> getAllOrder();

    Order findOrderById(UUID orderId);

    void createRestOrder(HttpServletRequest request);

    List<SalesDTO> getDailySalesBySellerId(UUID sellerId);

    Order changeStatusOrder(UpdateOrderRequestDTO orderDTO);
}
