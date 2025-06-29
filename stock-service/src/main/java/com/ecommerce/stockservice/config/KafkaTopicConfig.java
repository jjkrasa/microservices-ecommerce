package com.ecommerce.stockservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic stockReservationFailedTopic() {
        return TopicBuilder
                .name("stock-reservation-failed")
                .partitions(3)
                .replicas(1)
                .build();
    }
}