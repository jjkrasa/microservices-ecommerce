package com.ecommerce.exceptionlib.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {

  private final Map<String, String> errors;

  public BadRequestException(String message) {
    super(message);
    errors = Map.of("message", message);
  }

  public BadRequestException(Map<String, String> errors) {
    super("Invalid input");
    this.errors = errors;
  }
}
