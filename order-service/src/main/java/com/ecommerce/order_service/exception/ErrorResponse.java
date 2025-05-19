package com.ecommerce.order_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String errorCode,
        Map<String, String> errors
) {
}
