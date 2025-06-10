package com.ecommerce.cartservice.cart_service.util;

import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.UUID;

public class CookieUtil {

    private CookieUtil() {}

    public static ResponseCookie createSessionCookie() {
        String sessionId = UUID.randomUUID().toString();

        return ResponseCookie.from("sessionId", sessionId)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();
    }
}
