package com.apapedia.user.setting;

import io.github.cdimascio.dotenv.Dotenv;

public class Setting {
    public static final String ORDER_SERVER_URL = Dotenv.load().get("ORDER_SERVER_URL",
            "http://apap-190.cs.ui.ac.id/api/order");
    public static final String CART_SERVER_URL = Dotenv.load().get("CART_SERVER_URL",
            "http://apap-190.cs.ui.ac.id/api/cart");
    public static final String USER_SERVER_URL = Dotenv.load().get("USER_SERVER_URL",
            "http://apap-188.cs.ui.ac.id/api");
    public static final String CATALOG_SERVER_URL = Dotenv.load().get("CATALOG_SERVER_URL",
            "http://apap-189.cs.ui.ac.id/api/catalog");
    public static final String CATEGORY_SERVER_URL = Dotenv.load().get("CATEGORY_SERVER_URL",
            "http://apap-189.cs.ui.ac.id/api/category");
}
