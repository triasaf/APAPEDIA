package com.apapedia.order.restService;

import com.apapedia.order.dto.request.CatalogDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CartRestServiceImpl implements CartRestService {
    private final String catalogAPIBaseUrl = "http://localhost:8081"; // Replace with API base URL

    @Autowired
    private CartDb cartDb;
    @Autowired
    private CartItemDb cartItemDb;

    // Order Service #1
    @Override
    public Cart createRestCart(Cart cart) {

        return cartDb.save(cart);}

    // Order Service #2
    @Override
    public CartItem createRestCartItem(CartItem cartItem) {
        RestTemplate restTemplate = new RestTemplate();

        return cartItemDb.save(cartItem);}

    @Override
    public List<Cart> getAllCart() {return cartDb.findAll();}

    @Override
    public Cart findCartById(UUID userId) {
        try {
            for (Cart cart : getAllCart()) {
                if(cart.getUserId().equals(userId)) {
                    return cart;
                }
            }
            System.out.println("Cart not found for ID: " + userId);
            throw new Exception("Cart not found for ID: " + userId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Cart findCartByCartItemId(UUID cartId) {
        try {
            for (Cart cart : getAllCart()) {
                if(cart.getId().equals(cartId)) {
                    return cart;
                }
            }
            System.out.println("Cart not found for cartItem ID: " + cartId);
            throw new Exception("Cart not found for cartItem ID: " + cartId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
