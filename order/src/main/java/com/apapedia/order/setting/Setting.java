package com.apapedia.order.setting;

public class Setting {
    public static final String ORDER_SERVER_URL =
            System.getenv("ORDER_SERVER_URL") == null ?
                    "http://apap-190.cs.ui.ac.id/api/order" : System.getenv("ORDER_SERVER_URL");
    public static final String CART_SERVER_URL =
            System.getenv("CART_SERVER_URL") == null ?
                    "http://apap-190.cs.ui.ac.id/api/cart" : System.getenv("CART_SERVER_URL");
    public static final String USER_SERVER_URL =
            System.getenv("USER_SERVER_URL") == null ?
                    "http://apap-188.cs.ui.ac.id/api" : System.getenv("USER_SERVER_URL");
    public static final String CATALOG_SERVER_URL =
            System.getenv("CATALOG_SERVER_URL") == null ?
                    "http://apap-189.cs.ui.ac.id/api/catalog" : System.getenv("CATALOG_SERVER_URL");
    public static final String CATEGORY_SERVER_URL =
            System.getenv("CATEGORY_SERVER_URL") == null ?
                    "http://apap-189.cs.ui.ac.id/api/category" : System.getenv("CATEGORY_SERVER_URL");
}
