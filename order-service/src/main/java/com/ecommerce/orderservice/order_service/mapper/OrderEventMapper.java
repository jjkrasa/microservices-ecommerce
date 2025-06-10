package com.ecommerce.orderservice.order_service.mapper;

import com.ecommerce.orderservice.order_service.kafka.event.OrderCreatedEvent;
import com.ecommerce.orderservice.order_service.model.Order;
import com.ecommerce.orderservice.order_service.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "items", source = "items")
    OrderCreatedEvent ordertToOrderCreatedEvent(Order order);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "price")
    OrderCreatedEvent.OrderItem orderItemToOrderCreatedEventItem(OrderItem orderItem);

    List<OrderCreatedEvent.OrderItem> orderItemsToOrderCreatedEventItems(List<OrderItem> orderItems);
}
