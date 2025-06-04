package com.ecommerce.orderservice.order_service.service;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.orderservice.order_service.client.CartClient;
import com.ecommerce.orderservice.order_service.dto.CartResponse;
import com.ecommerce.orderservice.order_service.dto.OrderResponse;
import com.ecommerce.orderservice.order_service.mapper.OrderMapper;
import com.ecommerce.orderservice.order_service.model.Order;
import com.ecommerce.orderservice.order_service.model.OrderItem;
import com.ecommerce.orderservice.order_service.model.OrderStatus;
import com.ecommerce.orderservice.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CartClient cartClient;

    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(Long userId) {
        CartResponse cart = cartClient.getCart(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException(ErrorCode.CART_IS_EMPTY.getMessage());
        }

        cart
                .getItems()
                .stream()
                .filter(item -> item.getQuantity() > item.getProduct().availableQuantity())
                .findFirst()
                .ifPresent(item -> {
                    throw new BadRequestException(ErrorCode.INSUFFICIENT_STOCK.getMessage());
                });

        final Order order = Order.builder()
                .userId(userId)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        List<OrderItem> orderItems = cart
                .getItems()
                .stream()
                .map(cartItem -> OrderItem.builder()
                        .productId(cartItem.getProduct().id())
                        .name(cartItem.getProduct().name())
                        .price(cartItem.getProduct().price())
                        .quantity(cartItem.getQuantity())
                        .order(order)
                        .build()
                )
                .toList();

        BigDecimal totalAmount = orderItems
                .stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        cartClient.clearCart(userId);

        return orderMapper.orderToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getUsersOrders(Long userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(orderMapper::orderToOrderResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = getOrderByIdAndUserIdOrThrow(orderId, userId);

        return orderMapper.orderToOrderResponse(order);
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = getOrderByIdAndUserIdOrThrow(orderId, userId);

        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new BadRequestException(ErrorCode.ORDER_CANNOT_BE_CANCELLED.getMessage());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private Order getOrderByIdAndUserIdOrThrow(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND.getMessage()));
    }
}
