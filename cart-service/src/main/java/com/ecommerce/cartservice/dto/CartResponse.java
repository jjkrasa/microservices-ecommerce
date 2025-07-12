package com.ecommerce.cartservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;

    private Long userId;

    private List<CartItemResponse> items;
}
