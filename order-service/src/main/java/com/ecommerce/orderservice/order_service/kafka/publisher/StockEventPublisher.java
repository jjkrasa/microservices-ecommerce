package com.ecommerce.orderservice.order_service.kafka.publisher;

import com.ecommerce.orderservice.order_service.kafka.event.StockReserveRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(StockReserveRequestedEvent event) {
        log.info("Publishing stock reserve requested event: {}", event);
        kafkaTemplate.send("stock-reserve-requested", String.valueOf(event.getOrderId()), event);
    }
}
