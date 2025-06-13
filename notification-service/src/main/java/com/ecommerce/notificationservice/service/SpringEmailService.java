package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.OrderCreatedEvent;
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
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,  "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(event.getEmail());
            helper.setSubject("Order Confirmation");
            helper.setText(OrderHtmlBuilder.buildHtmlContent(event), true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
