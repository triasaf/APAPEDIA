package com.apapedia.frontend.setting;

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
    @Value("${apapedia.frontend.url}")
    public String FRONTEND_URL;
    @Value("${apapedia.image.url}")
    public String IMAGE_URL;

    final public String SERVER_BASE_URL = "https://sso.ui.ac.id/cas2";

    final public String SERVER_LOGIN = SERVER_BASE_URL + "/login?service=";

    final public String SERVER_LOGOUT = SERVER_BASE_URL + "/logout?url=";

    final public String SERVER_VALIDATE_TICKET = SERVER_BASE_URL + "/serviceValidate?ticket=%s&service=%s";
}
