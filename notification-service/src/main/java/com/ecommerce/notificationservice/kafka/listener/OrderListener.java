package com.ecommerce.notificationservice.kafka.listener;

import com.ecommerce.notificationservice.dto.OrderCreatedEvent;
import com.ecommerce.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderListener {

    private final EmailService emailService;

    @KafkaListener(
            topics = "order-created",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderCreatedListenerContainerFactory"
    )
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        emailService.sendOrderConfirmationEmail(event);
    }
}
