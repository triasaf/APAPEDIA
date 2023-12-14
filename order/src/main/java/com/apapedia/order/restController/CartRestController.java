package com.apapedia.order.restController;

import com.apapedia.order.dto.CartItemMapper;
import com.apapedia.order.dto.CartMapper;
import com.apapedia.order.dto.request.CreateCartItemRequestDTO;
import com.apapedia.order.dto.request.CreateCartRequestDTO;
import com.apapedia.order.dto.request.UpdateCartItemRequestDTO;
import com.apapedia.order.dto.response.ResponseAPI;
import com.apapedia.order.model.Cart;
import com.apapedia.order.restService.CartRestService;
import com.apapedia.order.security.jwt.JwtUtils;
import com.apapedia.order.setting.Setting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {
    @Autowired
    private CartRestService cartRestService;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private Setting setting;
    @Autowired
    private JwtUtils jwtUtils;

    // Get cart by cart Id
    @GetMapping("/{cartId}")
    public ResponseAPI restGetCart(@Valid @PathVariable("cartId") UUID cartId) {
        var response = new ResponseAPI<>();

        response.setResult(cartRestService.findCartByCartId(cartId));
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.name());

        return response;
    }

    // Get cart by user Id
    @GetMapping("")
    public ResponseAPI restGetCartByUserId(HttpServletRequest request) {

        UUID customerId = null;

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            customerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }

        var response = new ResponseAPI<>();

        response.setResult(cartRestService.findCartByUserId(customerId));
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.name());

        return response;
    }

    // Order service 1: Menambahkan Cart baru yang terhubung dengan user (customer)
    // baru
    @PostMapping("/create")
    public ResponseAPI restAddCart(@Valid @RequestBody CreateCartRequestDTO createCartRequestDTO,
            BindingResult bindingResult) {
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
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    @PostMapping("/add-item")
    public ResponseAPI restAddCartItem(
            @Valid @RequestBody CreateCartItemRequestDTO createCartItemRequestDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        UUID customerId = null;
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            customerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }

        var response = new ResponseAPI<>();
        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() - 1)
                    res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        var cartItem = cartItemMapper.createCartItemRequestDTOtoCartItem(createCartItemRequestDTO);

        try {
            response.setResult(cartRestService.createRestCartItem(cartItem, customerId));
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
        } catch (Exception e) {
            response.setResult(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
        }
        return response;
    }

    // Order Service 4: Get cart_items by user_id
    @GetMapping("/cart-items")
    public ResponseAPI restGetCartItems(HttpServletRequest request) {
        UUID customerId = null;

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            var token = headerAuth.substring(7);
            customerId = UUID.fromString(jwtUtils.getClaimFromJwtToken(token, "userId"));
        }

        var response = new ResponseAPI<>();

        try {
            Cart cart = cartRestService.findCartByUserId(customerId);

            response.setResult(cart.getListCartItem());
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        }
        return response;
    }

    // Order Service 3: PUT cart_items edit quantity
    @PutMapping("/cart-item/update")
    public ResponseAPI restEditCartItemsQuantity(@Valid @RequestBody UpdateCartItemRequestDTO cartItemDTO,
            BindingResult bindingResult) {
        var response = new ResponseAPI<>();

        if (bindingResult.hasErrors()) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < bindingResult.getErrorCount(); i++) {
                res.append(bindingResult.getFieldErrors().get(i).getDefaultMessage());
                if (i != bindingResult.getErrorCount() - 1)
                    res.append(", ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.name());
            response.setError(res.toString());
            return response;
        }

        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
            response.setResult(cartRestService.updateCartItemQuantity(cartItemDTO));
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setResult(e.getMessage());
        }
        return response;
    }

    // Order Service 5: DELETE cart item
    @DeleteMapping("/cart-item/{cartItemId}/delete")
    public ResponseAPI restDeleteCartItem(@PathVariable("cartItemId") UUID cartItemId) {
        var response = new ResponseAPI<>();

        try {
            cartRestService.deleteCartItem(cartItemId);

            response.setResult("Cart Item with ID:" + cartItemId + " has been successfully deleted");
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.name());
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(HttpStatus.NOT_FOUND.name());
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            response.setResult(e.getMessage());
        }
        return response;
    }

}
