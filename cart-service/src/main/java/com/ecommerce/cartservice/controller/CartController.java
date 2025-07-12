package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.AddCartItemRequest;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.UpdateCartItemRequest;
import com.ecommerce.cartservice.service.CartService;
import com.ecommerce.cartservice.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId, null));
    }

    @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid AddCartItemRequest request
    ) {
        cartService.addItemToCart(userId, null, request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId, null);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("cartItemId") Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        cartService.updateCartItemQuantity(userId, null, cartItemId, request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("cartItemId") Long cartItemId
    ) {
        cartService.deleteCartItem(userId, null, cartItemId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/anonymous")
    public ResponseEntity<CartResponse> getAnonymousCart(
            @CookieValue(name = "sessionId", required = false) String sessionId,
            HttpServletResponse response
    ) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();

            response.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.createSessionCookie().toString());
        }

        return ResponseEntity.ok(cartService.getCart(null, sessionId));
    }

    @PostMapping("/anonymous")
    public ResponseEntity<Void> addItemToAnonymousCart(
            @CookieValue(name = "sessionId", required = false) String sessionId,
            @RequestBody @Valid AddCartItemRequest request,
            HttpServletResponse response
    ) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();

            response.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.createSessionCookie().toString());
        }

        cartService.addItemToCart(null, sessionId, request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/anonymous")
    public ResponseEntity<Void> clearAnonymousCart(@CookieValue(name = "sessionId", required = false) String sessionId) {
        cartService.clearCart(null, sessionId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/anonymous/items/{cartItemId}")
    public ResponseEntity<Void> updateAnonymousCartItemQuantity(
            @CookieValue(name = "sessionId", required = false) String sessionId,
            @PathVariable("cartItemId") Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequest request
    ) {
        cartService.updateCartItemQuantity(null, sessionId, cartItemId, request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/anonymous/items/{cartItemId}")
    public ResponseEntity<Void> deleteAnonymousCartItem(
            @CookieValue(name = "sessionId", required = false) String sessionId,
            @PathVariable("cartItemId") Long cartItemId
    ) {
        cartService.deleteCartItem(null, sessionId, cartItemId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
