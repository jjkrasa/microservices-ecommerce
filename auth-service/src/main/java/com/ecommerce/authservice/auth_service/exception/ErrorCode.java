package com.ecommerce.authservice.auth_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT("Invalid input"),
    EMAIL_ALREADY_EXISTS("User with this email already exists"),
    PASSWORDS_DO_NOT_MATCH("Passwords do not match"),
    INVALID_CREDENTIALS("Invalid email or password");

    private final String message;

    public static Optional<ErrorCode> getErrorCodeFromMessage(String message) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getMessage().equals(message))
                .findFirst();
    }
}
