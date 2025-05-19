package com.ecommerce.order_service.mapper;

import com.ecommerce.order_service.dto.OrderItemResponse;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", source = "orderStatus")
    OrderResponse orderToOrderResponse(Order order);

    OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem);
}
