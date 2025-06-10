package com.ecommerce.exceptionlib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT("Invalid input"),

    MISSING_USER_OR_SESSION("User or session must be provided"),

    EMAIL_ALREADY_EXISTS("User with this email already exists"),
    PASSWORDS_DO_NOT_MATCH("Passwords do not match"),
    INVALID_CREDENTIALS("Invalid email or password"),

    PRODUCT_NOT_FOUND("Product was not found"),
    CATEGORY_NOT_FOUND("Category was not found"),
    IMAGE_UPLOAD_FAILED("Failed to store image"),
    DELETE_IMAGE_FAILED("Failed to delete image"),
    CATEGORY_IN_USE("Category is in use and cannot be deleted"),
    INVALID_IMAGE_FORMAT("Image must be either in JPG or PNG format"),

    CART_DOES_NOT_EXIST("Cart does not exist"),
    PRODUCT_WAS_NOT_FOUND_IN_CART("Product was not found in cart"),
    SESSION_ID_IS_EMPTY("Session id is empty"),

    CART_IS_EMPTY("Cart is empty"),
    INSUFFICIENT_STOCK("Insufficient stock for product"),
    ORDER_CANNOT_BE_CANCELLED("Only orders with created status can be cancelled"),
    ORDER_NOT_FOUND("Order was not found"),

    ;

    private final String message;

    public static Optional<ErrorCode> getErrorCodeFromMessage(final String message) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getMessage().equals(message))
                .findFirst();
    }
}
