package com.ecommerce.orderservice.mapper;

import com.ecommerce.orderservice.dto.CreateOrderRequest;
import com.ecommerce.orderservice.dto.OrderItemResponse;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", source = "orderStatus")
    OrderResponse orderToOrderResponse(Order order);

    OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem);


    Order createOrderRequestToOrder(CreateOrderRequest createOrderRequest);
}
