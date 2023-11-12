package com.apapedia.order.restService;

import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartRestServiceImpl implements CartRestService {
    @Autowired
    private CartDb cartDb;
    @Autowired
    private CartItemDb cartItemDb;

    // Order Service #1
    @Override
    public Cart createRestCart(Cart cart) {return cartDb.save(cart);}

    // Order Service #2
    @Override
    public CartItem createRestCartItem(CartItem cartItem) {return cartItemDb.save(cartItem);}

    @Override
    public List<Cart> getAllCart() {return cartDb.findAll();}

    @Override
    public Cart findCartById(UUID cartId) {
        try {
            for (Cart cart : getAllCart()) {
                if(cart.getId().equals(cartId)) {
                    return cart;
                }
            }
            System.out.println("Cart not found for ID: " + cartId);
            throw new Exception("Cart not found for ID: " + cartId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
