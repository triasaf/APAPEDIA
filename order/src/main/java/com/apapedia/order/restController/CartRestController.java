package com.apapedia.order.restController;

import com.apapedia.order.dto.CartItemMapper;
import com.apapedia.order.dto.CartMapper;
import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.model.Cart;
import com.apapedia.order.model.CartItem;
import com.apapedia.order.restService.CartRestService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/cart")
public class CartRestController {
    @Autowired
    private CartRestService cartRestService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CartItemMapper cartItemMapper;



    // Order service 1: Menambahkan Cart baru yang terhubung dengan user (customer) baru
    @PostMapping("/create")
    public ResponseAPI restAddCart(@Valid @RequestBody CreateCartRequestDTO createCartRequestDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                res.append(error.getDefaultMessage()).append(" ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
        }

        try {
            // Business Logic --> Oper ke RestService
            var cart = cartMapper.createCartRequestDTOToCart(createCartRequestDTO);
            response.setResult(cartRestService.createRestCart(cart));

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
        } catch (DataIntegrityViolationException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 2: Menambahkan item(Catalog) ke Cart
    @PostMapping("/add-item")
    public ResponseAPI restAddCartItem(@Valid @RequestBody CreateCartItemRequestDTO createCartItemRequestDTO, BindingResult bindingResult) {
        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                res.append(error.getField()).append(" ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            // Business Logic --> Oper ke RestService
            var cartItem = cartItemMapper.createCartItemRequestDTOtoCartItem(createCartItemRequestDTO);
            response.setResult(cartRestService.createRestCartItem(cartItem));

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
        } catch (DataIntegrityViolationException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 4:  Get cart_items by user_id
    @GetMapping("/{cartId}/cart-items")
    public ResponseAPI restGetCartItems(@Valid @PathVariable("cartId") UUID cartId) {
        var response = new ResponseAPI<>();
        // if (bindingResult.hasErrors()) {
        //     StringBuilder res = new StringBuilder();
        //     for (FieldError error : bindingResult.getFieldErrors()) {
        //         res.append(error.getField()).append(" ");
        //     }
        //     response.setStatus(HttpStatus.BAD_REQUEST.value());
        //     response.setMessage(HttpStatus.BAD_REQUEST.name());
        //     response.setError(res.toString());
        //     return response;
        // }

        try {
            Cart cart = cartRestService.findCartById(cartId);
            
            response.setResult(cart.getListCartItem());

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
        } catch (DataIntegrityViolationException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }
        return response;
    }


}


