package com.apapedia.order.restService;

import java.util.List;
import java.util.UUID;

import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;

public interface CartRestService {
    Cart createRestCart(Cart cart);

    CartItem createRestCartItem(CartItem cartItem, UUID customerId);

    Cart findCartByUserId(UUID userId);

    Cart findCartByCartId(UUID userId);

    Cart findCartByCartItemId(UUID cartId);

    CartItem findCartItemById(UUID cartItemId);

    List<Cart> getAllCart();

    List<CartItem> getAllCartItem();

    CartItem updateCartItemQuantity(UpdateCartItemRequestDTO cartItemDTO);

    void deleteCartItem(UUID cartItemId);
}
