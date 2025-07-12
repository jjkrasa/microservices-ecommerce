package com.ecommerce.cartservice.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String category,
        String description,
        BigDecimal price,
        Integer availableQuantity,
        String imageUrl
) {
}
