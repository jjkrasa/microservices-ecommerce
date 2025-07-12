package com.ecommerce.productservice.dto;

public record StockResponse(
        Long productId,
        Integer availableQuantity
) {
}
