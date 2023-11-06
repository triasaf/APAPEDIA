package com.apapedia.order.restController;

import com.apapedia.order.restService.OrderRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderRestController {
    @Autowired
    private OrderRestService orderRestService;
}
