package com.apapedia.user.setting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Setting {
    @Value("${apapedia.user.url}")
    public String userServerUrl;
    @Value("${apapedia.catalog.url}")
    public String catalogServerUrl;
    @Value("${apapedia.category.url}")
    public String categoryServerUrl;
    @Value("${apapedia.order.url}")
    public String orderServerUrl;
    @Value("${apapedia.cart.url}")
    public String CART_SERVER_URL;
    @Value("${apapedia.image.url}")
    public String imageUrl;
}
