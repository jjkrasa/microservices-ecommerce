package com.ecommerce.notificationservice.listener;

import com.ecommerce.notificationservice.dto.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    @KafkaListener(
            topics = "order-created",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {

    }
}
