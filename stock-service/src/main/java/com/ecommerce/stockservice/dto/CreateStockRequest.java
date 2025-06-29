package com.ecommerce.stockservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockRequest {

    @Min(value = 0, message = "Available quantity must be positive")
    @NotNull(message = "Available quantity is required")
    private Integer availableQuantity;
}
