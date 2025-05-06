package com.ecommerce.productservice.product_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT("Invalid input"),
    PRODUCT_NOT_FOUND("Product was not found"),
    CATEGORY_NOT_FOUND("Category was not found"),
    IMAGE_UPLOAD_FAILED("Failed to store image"),
    CATEGORY_IN_USE("Category is in use and cannot be deleted"),
    INVALID_IMAGE_FORMAT("Image must be either in JPG or PNG format");

    private final String message;

    public static Optional<ErrorCode> getErrorCodeFromMessage(String message) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getMessage().equals(message))
                .findFirst();
    }
}
