package com.ecommerce.orderservice.order_service.client;

import com.ecommerce.orderservice.order_service.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-service", path = "/api/carts")
public interface CartClient {

    @GetMapping
    CartResponse getCart(@RequestHeader("X-User-Id") Long userId);

    @DeleteMapping
    void clearCart(@RequestHeader("X-User-Id") Long userId);

    @GetMapping("/anonymous")
    CartResponse getAnonymousCart(@CookieValue(name = "sessionId") String sessionId);

    @DeleteMapping("/anonymous")
    void clearAnonymousCart(@CookieValue(name = "sessionId") String sessionId);
}
