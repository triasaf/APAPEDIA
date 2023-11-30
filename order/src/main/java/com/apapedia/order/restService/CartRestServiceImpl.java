package com.apapedia.order.restService;

import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.dto.request.CatalogDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.repository.CartDb;
import com.apapedia.order.repository.CartItemDb;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.xml.catalog.Catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CartRestServiceImpl implements CartRestService { 

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
    public List<CartItem> getAllCartItem() {return cartItemDb.findAll();}
    public Cart findCartByUserId(UUID userId) {
        for (Cart cart : getAllCart()) {
            if(cart.getUserId().equals(userId)) {
                return cart;
            }
        }
        System.out.println("Cart not found for User ID: " + userId);
        throw new NoSuchElementException("Cart not found for User ID: " + userId);
    }

    @Override
    public Cart findCartByCartItemId(UUID cartId) {

        for (Cart cart : getAllCart()) {
            if(cart.getId().equals(cartId)) {
                return cart;
            }
        }
        System.out.println("Cart not found for cartItem ID: " + cartId);
        throw new NoSuchElementException("Cart not found for cartItem ID: " + cartId);
    }
        
    @Override
    public CartItem findCartItemById(UUID cartItemId) {
        for (CartItem cartItem : getAllCartItem()) {
            if(cartItem.getId().equals(cartItemId)) {
                return cartItem;
            }
        }
        System.out.println("Cart Item not found for ID: " + cartItemId);
        throw new NoSuchElementException("Cart Item not found for ID: " + cartItemId);
    }

    @Override
    public Cart findCartByCartId(UUID cartId) {
        for (Cart cart : getAllCart()) {
            if(cart.getId().equals(cartId)) {
                return cart;
            }
        }
        System.out.println("Cart not found for ID: " + cartId);
        throw new NoSuchElementException("Cart not found for ID: " + cartId);
    }


    @Override
    public CartItem updateCartItemQuantity(UpdateCartItemRequestDTO cartItemDTO) {
        CartItem existingCartItem = findCartItemById(cartItemDTO.getId());

        existingCartItem.setQuantity(cartItemDTO.getQuantity());

        return cartItemDb.save(existingCartItem);
    }

    @Override
    public void deleteCartItem(UUID cartItemId) {
        CartItem cartItem = findCartItemById(cartItemId);

        cartItemDb.delete(cartItem);
    }


}
