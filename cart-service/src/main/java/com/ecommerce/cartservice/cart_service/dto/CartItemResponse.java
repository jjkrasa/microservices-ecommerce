package com.ecommerce.cartservice.cart_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;

    private ProductResponse product;

    private Integer quantity;
}
