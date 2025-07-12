package com.ecommerce.orderservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long productId;

    private String name;

    private BigDecimal price;

    private Integer quantity;
}
