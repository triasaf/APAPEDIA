package com.apapedia.order.restService;

import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;

public interface CartRestService {
    Cart createRestCart(Cart cart);

    CartItem createRestCartItem(CartItem cartItem);
}
