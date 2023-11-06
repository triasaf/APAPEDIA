package com.apapedia.order.restController;

import com.apapedia.order.restService.CartRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {
    @Autowired
    private CartRestService cartRestService;
}
