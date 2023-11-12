package com.apapedia.order.restService;

import java.util.List;
import java.util.UUID;

import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;

public interface CartRestService {
    Cart createRestCart(Cart cart);

    CartItem createRestCartItem(CartItem cartItem);

    Cart findCartById(UUID cartId);

    List<Cart> getAllCart();
}
