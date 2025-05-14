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
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(cartService.getCart(Long.parseLong(userId)));
    }

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid AddCartItemRequest request
    ) {
        cartService.addItemToCart(Long.parseLong(userId), request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") String userId) {
        cartService.clearCart(Long.parseLong(userId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        cartService.updateCartItemQuantity(Long.parseLong(userId), cartItemId, request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long cartItemId
    ) {
        cartService.deleteCartItem(Long.parseLong(userId), cartItemId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
