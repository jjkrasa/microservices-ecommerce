package com.ecommerce.exceptionlib;

import java.util.Map;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String errorCode,
        Map<String, String> errors
) {
}
