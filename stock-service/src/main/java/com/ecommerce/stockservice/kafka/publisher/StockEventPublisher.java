package com.ecommerce.stockservice.kafka.publisher;

import com.ecommerce.stockservice.kafka.event.StockReservationFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishReservationFailed(StockReservationFailedEvent event) {
        System.out.println("Publishing Stock Reservation Failed Event");
        kafkaTemplate.send("stock-reservation-failed", String.valueOf(event.getOrderId()), event);
    }
}
