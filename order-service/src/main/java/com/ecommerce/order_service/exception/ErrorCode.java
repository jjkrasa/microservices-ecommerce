package com.ecommerce.order_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CART_IS_EMPTY("Cart is empty"),
    INSUFFICIENT_STOCK("Insufficient stock for product"),
    ORDER_CANNOT_BE_CANCELLED("Only orders with created status can be cancelled"),
    ORDER_NOT_FOUND("Order was not found"),
    CART_DOES_NOT_EXIST("Cart does not exist"),
    INVALID_INPUT("Invalid input");

    private final String message;

    public static Optional<ErrorCode> getErrorCodeFromMessage(String message) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getMessage().equals(message))
                .findFirst();
    }
}
