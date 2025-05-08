package com.ecommerce.cartservice.cart_service.controller;

import com.ecommerce.cartservice.cart_service.dto.AddCartItemRequest;
import com.ecommerce.cartservice.cart_service.dto.CartResponse;
import com.ecommerce.cartservice.cart_service.dto.UpdateCartItemRequest;
import com.ecommerce.cartservice.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        Long userId = extractUserId(authentication);

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            Authentication authentication,
            @RequestBody @Valid AddCartItemRequest request
    ) {
        Long userId = extractUserId(authentication);
        cartService.addItemToCart(userId, request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Long userId = extractUserId(authentication);
        cartService.clearCart(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        Long userId = extractUserId(authentication);
        cartService.updateCartItemQuantity(userId, cartItemId, request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            Authentication authentication,
            @PathVariable Long cartItemId
    ) {
        Long userId = extractUserId(authentication);
        cartService.deleteCartItem(userId, cartItemId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Long extractUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
