package com.ecommerce.stockservice.dto;

import jakarta.validation.constraints.Max;
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
public class UpdateStockRequest {

    @Min(value = -100, message = "Quantity change must be higher than -100")
    @Max(value = 100, message = "Quantity change must be lower than 100")
    @NotNull(message = "Quantity change is required")
    private Integer quantityChange;
}
