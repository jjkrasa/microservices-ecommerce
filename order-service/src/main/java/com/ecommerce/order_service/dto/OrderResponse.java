package com.ecommerce.order_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;

    private BigDecimal totalAmount;

    private String status;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}
