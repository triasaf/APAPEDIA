package com.apapedia.order.restService;

import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartRestServiceImpl implements CartRestService {
    @Autowired
    private CartDb cartDb;
    @Autowired
    private CartItemDb cartItemDb;
}
