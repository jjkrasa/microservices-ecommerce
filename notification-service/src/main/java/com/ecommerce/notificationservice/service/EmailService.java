package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.kafka.event.OrderCancelledEvent;
import com.ecommerce.notificationservice.kafka.event.OrderCreatedEvent;

public interface EmailService {

    void sendOrderConfirmationEmail(OrderCreatedEvent event);

    void sendOrderCancelledEmail(OrderCancelledEvent event);
}
