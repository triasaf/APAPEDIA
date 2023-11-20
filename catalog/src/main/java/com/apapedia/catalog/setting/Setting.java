package com.apapedia.catalog.setting;

public class Setting {
    public static final String ORDER_SERVER_URL =
            System.getenv("ORDER_SERVER_URL") == null ?
                    "http://localhost:8080/api/order" : System.getenv("ORDER_SERVER_URL");
    public static final String CART_SERVER_URL =
            System.getenv("CART_SERVER_URL") == null ?
                    "http://localhost:8080/api/cart" : System.getenv("CART_SERVER_URL");
    public static final String USER_SERVER_URL =
            System.getenv("USER_SERVER_URL") == null ?
                    "http://localhost:8082/api" : System.getenv("USER_SERVER_URL");
}
