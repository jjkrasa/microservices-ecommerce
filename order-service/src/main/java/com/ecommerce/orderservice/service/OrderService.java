package com.ecommerce.orderservice.service;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.orderservice.client.CartClient;
import com.ecommerce.orderservice.dto.CartResponse;
import com.ecommerce.orderservice.dto.CreateOrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.kafka.publisher.OrderEventPublisher;
import com.ecommerce.orderservice.kafka.publisher.StockEventPublisher;
import com.ecommerce.orderservice.kafka.event.OrderCancelledEvent;
import com.ecommerce.orderservice.kafka.event.OrderCreatedEvent;
import com.ecommerce.orderservice.kafka.event.StockReserveRequestedEvent;
import com.ecommerce.orderservice.mapper.OrderEventMapper;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
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

    private final OrderEventPublisher orderEventPublisher;

    private final OrderEventMapper orderEventMapper;

    private final StockEventPublisher stockEventPublisher;

    @Transactional
    public OrderResponse createOrder(Long userId, String sessionId, CreateOrderRequest createOrderRequest) {
        CartResponse cart = null;
        if (userId != null) {
            cart = cartClient.getCart(userId);
        } else if (sessionId != null) {
            cart = cartClient.getAnonymousCart(sessionId);
        }

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
                .email(createOrderRequest.email())
                .firstName(createOrderRequest.firstName())
                .lastName(createOrderRequest.lastName())
                .phoneNumber(createOrderRequest.phoneNumber())
                .street(createOrderRequest.street())
                .houseNumber(createOrderRequest.houseNumber())
                .city(createOrderRequest.city())
                .zipCode(createOrderRequest.zipCode())
                .country(createOrderRequest.country())
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

        OrderCreatedEvent orderCreatedEvent = orderEventMapper.ordertToOrderCreatedEvent(savedOrder);

        orderEventPublisher.publishOrderCreatedEvent(orderCreatedEvent);

        List<StockReserveRequestedEvent.ReserveItem> reserveItems = savedOrder.getItems()
                .stream()
                .map(item -> new StockReserveRequestedEvent.ReserveItem(item.getProductId(), item.getQuantity()))
                .toList();
        StockReserveRequestedEvent stockReserveRequestedEvent = new StockReserveRequestedEvent(savedOrder.getId(), reserveItems);
        stockEventPublisher.publish(stockReserveRequestedEvent);

        if (userId != null) {
            cartClient.clearCart(userId);
        } else if (sessionId != null) {
            cartClient.clearAnonymousCart(sessionId);
        }

        return orderMapper.orderToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUsersOrders(Long userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(orderMapper::orderToOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            orderEventPublisher.publishOrderCancelledEvent(new OrderCancelledEvent(orderId, order.getEmail(), reason));
        });
    }

    private Order getOrderByIdAndUserIdOrThrow(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND.getMessage()));
    }
}
