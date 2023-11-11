package com.apapedia.user.setting;

public class Setting {
    public static final String ORDER_SERVER_URL =
            System.getenv("ORDER_SERVER_URL") == null ?
                    "http://localhost:8080/api/order" : System.getenv("ORDER_SERVER_URL");
    public static final String CART_SERVER_URL =
            System.getenv("CART_SERVER_URL") == null ?
                    "http://localhost:8080/api/cart" : System.getenv("CART_SERVER_URL");
    public static final String CATALOG_SERVER_URL =
            System.getenv("CATALOG_SERVER_URL") == null ?
                    "http://localhost:8081/api/catalog" : System.getenv("CATALOG_SERVER_URL");
}
