package com.ecommerce.orderservice.order_service.kafka.listener;

import com.ecommerce.orderservice.order_service.kafka.event.StockReservationFailedEvent;
import com.ecommerce.orderservice.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final OrderService orderService;

    @KafkaListener(
            topics = "stock-reservation-failed",
            groupId = "order-service",
            containerFactory = "stockReservationFailedListenerFactory"
    )
    public void handleStockReservationFailed(StockReservationFailedEvent event) {
        log.info("StockEventListener handleStockReservationFailed - reason: {}", event.getReason());
        log.warn("Order reservation failed: {}", event);
        orderService.cancelOrder(event.getOrderId(), event.getReason());
    }
}
