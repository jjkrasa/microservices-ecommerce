package com.ecommerce.orderservice.kafka.publisher;

import com.ecommerce.orderservice.kafka.event.OrderCancelledEvent;
import com.ecommerce.orderservice.kafka.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        kafkaTemplate.send("order-created", String.valueOf(event.getOrderId()), event);
    }

    public void publishOrderCancelledEvent(OrderCancelledEvent event) {
        kafkaTemplate.send("order-cancelled", String.valueOf(event.getOrderId()), event);
    }
}
