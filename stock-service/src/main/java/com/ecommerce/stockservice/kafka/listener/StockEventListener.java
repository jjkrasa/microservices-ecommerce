package com.ecommerce.stockservice.kafka.listener;

import com.ecommerce.stockservice.dto.StockReserveRequestedEvent;
import com.ecommerce.stockservice.kafka.event.StockReservationFailedEvent;
import com.ecommerce.stockservice.kafka.publisher.StockEventPublisher;
import com.ecommerce.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final StockService stockService;

    private final StockEventPublisher stockEventPublisher;



    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000),
            dltTopicSuffix = ".dlt"
    )
    @KafkaListener(
            topics = "stock-reserve-requested",
            groupId = "stock-service",
            containerFactory = "stockReserveRequestedListenerFactory"
    )
    public void handleStockReserveRequested(StockReserveRequestedEvent event) {
        log.info("Received stock reservation request for order {}", event.getOrderId());

        stockService.reserveAllOrThrow(event);

        log.info("Stock reserved for order {}", event.getOrderId());
    }

    @DltHandler
    public void handleStockReserveRequestedDlt(
            StockReserveRequestedEvent event,
            @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage
    ) {
        log.warn("DLT: Failed to reserve stock for order {}: {}", event.getOrderId(), errorMessage);

        stockEventPublisher.publishReservationFailed(
                new StockReservationFailedEvent(event.getOrderId(), errorMessage)
        );
    }
}
