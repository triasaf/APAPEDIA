package com.apapedia.order.setting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Setting {
    @Value("${apapedia.user.url}")
    public String USER_SERVER_URL;
    @Value("${apapedia.catalog.url}")
    public String CATALOG_SERVER_URL;
    @Value("${apapedia.category.url}")
    public String CATEGORY_SERVER_URL;
    @Value("${apapedia.order.url}")
    public String ORDER_SERVER_URL;
    @Value("${apapedia.cart.url}")
    public String CART_SERVER_URL;
    @Value("${apapedia.image.url}")
    public String IMAGE_URL;
}

