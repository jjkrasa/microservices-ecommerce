package com.ecommerce.cartservice.cart_service.controller;

import com.ecommerce.cartservice.cart_service.dto.AddCartItemRequest;
import com.ecommerce.cartservice.cart_service.dto.CartResponse;
import com.ecommerce.cartservice.cart_service.dto.UpdateCartItemRequest;
import com.ecommerce.cartservice.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid AddCartItemRequest request
    ) {
        cartService.addItemToCart(userId, request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        cartService.updateCartItemQuantity(userId, cartItemId, request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cartItemId
    ) {
        cartService.deleteCartItem(userId, cartItemId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
