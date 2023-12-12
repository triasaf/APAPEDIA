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

import com.apapedia.order.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CartRestServiceImpl implements CartRestService {
    @Autowired
    private CartDb cartDb;
    @Autowired
    private CartItemDb cartItemDb;
    @Autowired
    private Setting setting;

    @Override
    public Cart createRestCart(Cart cart) {
        return cartDb.save(cart);
    }

    @Override
    public CartItem createRestCartItem(CartItem cartItem, UUID customerId) {
        var cart = findCartByCartId(cartItem.getCartId().getId());

        if (!cart.getUserId().equals(customerId)) {
            throw new UsernameNotFoundException("Unauthorized");
        }

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ResponseAPI<CatalogDTO>> catalogDTO = restTemplate.exchange(
                setting.CATALOG_SERVER_URL + "/" + cartItem.getProductId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        if (catalogDTO.getBody() != null && catalogDTO.getBody().getStatus() == 200) {
            //Cek stok product
            if (cartItem.getQuantity() > catalogDTO.getBody().getResult().getStok()) {
                throw new RestClientException("Insufficient product stock");
            }

            // Akses attribute price
            var productPrice = catalogDTO.getBody().getResult().getPrice();
            var productQuantity = cartItem.getQuantity();
            var totalPriceCartItem = productPrice * productQuantity;

            // Mendapat nilai totalprice di cart
            var currentTotalPrice = cart.getTotalPrice();
            var updatedTotalPrice = currentTotalPrice + totalPriceCartItem;

            //Mengatur nilai totalPrice di cart
            cart.setTotalPrice(updatedTotalPrice);
            // Menyimpan perubahan Cart di db
            createRestCart(cart);
        } else {
            throw new RestClientException(catalogDTO.getBody().getError());
        }

        return cartItemDb.save(cartItem);
    }

    @Override
    public List<Cart> getAllCart() {return cartDb.findAll();}

    @Override
    public List<CartItem> getAllCartItem() {return cartItemDb.findAll();}

    @Override
    public Cart findCartByUserId(UUID userId) {
        for (Cart cart : getAllCart()) {
            if(cart.getUserId().equals(userId)) {
                return cart;
            }
        }
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
            if (cart.getId().equals(cartId)) {
                return cart;
            }
        }
        throw new NoSuchElementException("Cart not found for ID: " + cartId);
    }

    @Override
    public CartItem updateCartItemQuantity(UpdateCartItemRequestDTO cartItemDTO) {
        CartItem existingCartItem = findCartItemById(cartItemDTO.getId());

        var currentQuantity = existingCartItem.getQuantity();
        var cart = findCartByCartId(existingCartItem.getCartId().getId());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ResponseAPI<CatalogDTO>> catalogDTO = restTemplate.exchange(
                setting.CATALOG_SERVER_URL + "/" + existingCartItem.getProductId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        if (catalogDTO.getBody() != null && catalogDTO.getBody().getStatus().equals(200)) {
            var productStock = catalogDTO.getBody().getResult().getStok();
            if (productStock < cartItemDTO.getQuantity()) {
                throw new RestClientException("Insufficient product stock");
            }

            var productPrice = catalogDTO.getBody().getResult().getPrice();

            var quantityDifference = cartItemDTO.getQuantity() - currentQuantity;
            var priceChanged = productPrice * quantityDifference;

            cart.setTotalPrice(cart.getTotalPrice() + priceChanged);
            cartDb.save(cart);
        } else {
            throw new RestClientException(catalogDTO.getBody().getError());
        }

        existingCartItem.setQuantity(cartItemDTO.getQuantity());

        return cartItemDb.save(existingCartItem);
    }

    @Override
    public void deleteCartItem(UUID cartItemId) {
        CartItem cartItem = findCartItemById(cartItemId);
        cartItemDb.delete(cartItem);
    }
}
