package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.kafka.event.OrderCancelledEvent;
import com.ecommerce.notificationservice.kafka.event.OrderCreatedEvent;
import com.ecommerce.notificationservice.utils.OrderHtmlBuilder;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Override
    public void sendOrderConfirmationEmail(OrderCreatedEvent event) {
        sendEmail(emailFrom, event.getEmail(), "Order Confirmation", OrderHtmlBuilder.buildOrderCreatedHtmlContent(event));
    }

    @Override
    public void sendOrderCancelledEmail(OrderCancelledEvent event) {
        sendEmail(emailFrom, event.getEmail(), "Order Cancelled", OrderHtmlBuilder.buildOrderCancelledHtmlContent(event));
    }

    private void sendEmail(String from, String to, String subject, String text) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,  "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
