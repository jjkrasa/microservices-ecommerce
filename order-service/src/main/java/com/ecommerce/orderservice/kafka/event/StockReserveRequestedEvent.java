package com.ecommerce.orderservice.kafka.event;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockReserveRequestedEvent {

    private Long orderId;

    List<ReserveItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReserveItem {

        private Long productId;

        private Integer quantity;
    }
}
