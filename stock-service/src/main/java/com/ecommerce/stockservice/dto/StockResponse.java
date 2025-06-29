package com.ecommerce.stockservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

    private Long productId;

    private Integer availableQuantity;
}
