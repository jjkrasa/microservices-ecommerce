package com.ecommerce.productservice.product_service.dto;

public record StockResponse(
        Long productId,
        Integer availableQuantity
) {
}
