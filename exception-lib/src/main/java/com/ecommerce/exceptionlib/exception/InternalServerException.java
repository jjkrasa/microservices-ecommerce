package com.ecommerce.exceptionlib.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException() {
    }

    public InternalServerException(String message) {
        super(message);
    }
}
