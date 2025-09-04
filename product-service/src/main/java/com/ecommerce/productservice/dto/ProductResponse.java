package com.ecommerce.productservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;

    private String name;

    private String category;

    private String description;

    private BigDecimal price;

    private Integer availableQuantity;

    private String imageUrl;
}
