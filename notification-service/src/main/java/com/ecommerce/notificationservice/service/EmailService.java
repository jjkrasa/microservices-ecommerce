package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.OrderCreatedEvent;

public interface EmailService {
    void sendOrderConfirmationEmail(OrderCreatedEvent event);
}
