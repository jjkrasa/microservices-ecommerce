package com.ecommerce.cartservice.cart_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CART_DOES_NOT_EXIST("Cart does not exist"),
    PRODUCT_WAS_NOT_FOUND_IN_CART("Product was not found in cart"),
    PRODUCT_DOES_NOT_EXIST("Product does not exist"),
    INVALID_INPUT("Invalid input");

    private final String message;

    public static Optional<ErrorCode> getErrorCodeFromMessage(String message) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getMessage().equals(message))
                .findFirst();
    }
}
