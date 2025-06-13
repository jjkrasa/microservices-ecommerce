package com.ecommerce.exceptionlib;

import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.ConflictException;
import com.ecommerce.exceptionlib.exception.InternalServerException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ErrorCode.INVALID_INPUT.name(),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            BadRequestException.class,
            NotFoundException.class,
            ConflictException.class,
            InternalServerException.class
    })
    public ResponseEntity<ErrorResponse> handleException(RuntimeException ex) {
        HttpStatus status = switch (ex) {
            case BadRequestException ignored -> HttpStatus.BAD_REQUEST;
            case NotFoundException ignored -> HttpStatus.NOT_FOUND;
            case ConflictException ignored -> HttpStatus.CONFLICT;
            case InternalServerException ignored -> HttpStatus.INTERNAL_SERVER_ERROR;
            case RuntimeException ignored -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                resolveErrorCode(ex.getMessage()),
                Map.of("message", status != HttpStatus.INTERNAL_SERVER_ERROR ? ex.getMessage() : "Internal Server Error")
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    private String resolveErrorCode(final String message) {
        return ErrorCode.getErrorCodeFromMessage(message)
                .map(Enum::name)
                .orElse("INTERNAL_SERVER_ERROR");
    }
}
