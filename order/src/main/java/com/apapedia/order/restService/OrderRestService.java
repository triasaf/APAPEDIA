package com.apapedia.order.restService;

import com.apapedia.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRestService {
    List<Order> findOrderByCustomerId(UUID customer);
    List<Order> findOrderBySellerId(UUID seller);

    List<Order> getAllOrder();
}
