package com.ecommerce.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder
                .name("order-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder
                .name("order-cancelled")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderReservedTopic() {
        return TopicBuilder
                .name("stock-reserve-requested")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
