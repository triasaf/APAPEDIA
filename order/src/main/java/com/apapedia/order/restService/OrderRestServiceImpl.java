package com.apapedia.order.restService;

import com.apapedia.order.repository.OrderDb;
import com.apapedia.order.repository.OrderItemDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderRestServiceImpl implements OrderRestService{
    @Autowired
    private OrderDb orderDb;
    @Autowired
    private OrderItemDb orderItemDb;
}
