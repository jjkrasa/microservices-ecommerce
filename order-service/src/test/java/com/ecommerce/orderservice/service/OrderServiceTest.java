package com.ecommerce.orderservice.service;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.orderservice.client.CartClient;
import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.kafka.event.OrderCancelledEvent;
import com.ecommerce.orderservice.kafka.event.OrderCreatedEvent;
import com.ecommerce.orderservice.kafka.event.StockReserveRequestedEvent;
import com.ecommerce.orderservice.kafka.publisher.OrderEventPublisher;
import com.ecommerce.orderservice.kafka.publisher.StockEventPublisher;
import com.ecommerce.orderservice.mapper.OrderEventMapper;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @Mock
    private OrderEventMapper orderEventMapper;

    @Mock
    private StockEventPublisher stockEventPublisher;

    private CreateOrderRequest createOrderRequest;

    private CartResponse cartWithOneItem;

    private Order order;

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest(
                "john.doe@example.com",
                "John",
                "Doe",
                "+420777777777",
                "Main Street",
                "12A",
                "Prague",
                "11000",
                "Czech Republic"
        );


        ProductResponse product = new ProductResponse(
                1L,
                "Product",
                "TECH",
                "Description",
                BigDecimal.valueOf(99.99),
                30,
                null
        );

        CartItemResponse cartItem = CartItemResponse.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        cartWithOneItem = CartResponse.builder()
                .id(100L)
                .userId(1L)
                .items(new ArrayList<>(List.of(cartItem)))
                .build();

        order = Order.builder()
                .id(1L)
                .userId(1L)
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+420777777777")
                .street("Main Street")
                .houseNumber("12A")
                .city("Prague")
                .zipCode("11000")
                .country("Czech Republic")
                .orderStatus(OrderStatus.CREATED)
                .totalAmount(BigDecimal.valueOf(199.98))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        orderResponse = OrderResponse.builder()
                .id(1L)
                .totalAmount(BigDecimal.valueOf(199.98))
                .status(OrderStatus.CREATED.name())
                .items(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("createOrder() tests")
    class CreateOrder {
        @Test
        public void createOrder_shouldCreateOrderForUser() {

            when(cartClient.getCart(1L)).thenReturn(cartWithOneItem);

            when(orderRepository.save(any(Order.class))).then(inv -> {
                Order order = inv.getArgument(0);
                order.setId(1L);

                return order;
            });

            OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
            when(orderEventMapper.ordertToOrderCreatedEvent(any(Order.class))).thenReturn(orderCreatedEvent);

            OrderResponse expectedResponse = OrderResponse.builder()
                    .id(1L)
                    .totalAmount(BigDecimal.valueOf(199.98))
                    .status(OrderStatus.CREATED.name())
                    .createdAt(LocalDateTime.now())
                    .items(Collections.emptyList())
                    .build();

            when(orderMapper.orderToOrderResponse(any(Order.class))).thenReturn(expectedResponse);

            OrderResponse result = orderService.createOrder(1L, null, createOrderRequest);

            assertEquals(expectedResponse, result);
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(orderEventPublisher, times(1)).publishOrderCreatedEvent(orderCreatedEvent);
            verify(stockEventPublisher, times(1)).publish(any(StockReserveRequestedEvent.class));
            verify(cartClient, times(1)).clearCart(1L);
        }

        @Test
        public void createOrder_shouldCreateOrderForAnonymous() {

            when(cartClient.getAnonymousCart("session")).thenReturn(cartWithOneItem);

            when(orderRepository.save(any(Order.class))).then(inv -> {
                Order order =  inv.getArgument(0);
                order.setId(1L);

                return order;
            });

            OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
            when(orderEventMapper.ordertToOrderCreatedEvent(any(Order.class))).thenReturn(orderCreatedEvent);

            OrderResponse expectedResponse = OrderResponse.builder()
                    .id(1L)
                    .totalAmount(BigDecimal.valueOf(199.98))
                    .status(OrderStatus.CREATED.name())
                    .createdAt(LocalDateTime.now())
                    .items(Collections.emptyList())
                    .build();

            when(orderMapper.orderToOrderResponse(any(Order.class))).thenReturn(expectedResponse);

            OrderResponse result = orderService.createOrder(null, "session", createOrderRequest);

            assertEquals(expectedResponse, result);
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(orderEventPublisher).publishOrderCreatedEvent(orderCreatedEvent);
            verify(stockEventPublisher).publish(any(StockReserveRequestedEvent.class));
            verify(cartClient).clearAnonymousCart("session");
        }

        @Test
        public void createOrder_shouldThrowBadRequestWhenCartIsEmpty() {
            cartWithOneItem.getItems().clear();

            when(cartClient.getAnonymousCart("session")).thenReturn(cartWithOneItem);

            BadRequestException ex = assertThrows(BadRequestException.class, () -> orderService.createOrder(null, "session", createOrderRequest));
            assertEquals(ErrorCode.CART_IS_EMPTY.getMessage(), ex.getMessage());
        }

        @Test
        public void createOrder_shouldThrowBadRequestWhenProductIsNotAvailable() {
            cartWithOneItem.getItems().getFirst().setQuantity(100);

            when(cartClient.getAnonymousCart("session")).thenReturn(cartWithOneItem);

            BadRequestException ex = assertThrows(BadRequestException.class, () -> orderService.createOrder(null, "session", createOrderRequest));
            assertEquals(ErrorCode.INSUFFICIENT_STOCK.getMessage(), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("getUsersOrders() tests")
    class GetUsersOrders {
        @Test
        public void getUsersOrders_shouldReturnUsersOrders() {
            Order order2 = Order.builder()
                    .id(2L)
                    .userId(1L)
                    .email("john.doe@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .phoneNumber("+420777777777")
                    .street("Main Street")
                    .houseNumber("12A")
                    .city("Prague")
                    .zipCode("11000")
                    .country("Czech Republic")
                    .orderStatus(OrderStatus.SHIPPED)
                    .totalAmount(BigDecimal.valueOf(10))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .items(Collections.emptyList())
                    .build();

            OrderResponse orderResponse2 = OrderResponse.builder()
                    .id(2L)
                    .totalAmount(BigDecimal.valueOf(10))
                    .status(OrderStatus.SHIPPED.name())
                    .items(Collections.emptyList())
                    .build();

            List<Order> orders = List.of(order, order2);
            List<OrderResponse> ordersResponse = List.of(orderResponse, orderResponse2);

            when(orderRepository.findAllByUserId(1L)).thenReturn(orders);

            when(orderMapper.orderToOrderResponse(order)).thenReturn(orderResponse);
            when(orderMapper.orderToOrderResponse(order2)).thenReturn(orderResponse2);

            List<OrderResponse> result = orderService.getUsersOrders(1L);

            assertIterableEquals(ordersResponse, result);
            verify(orderRepository, times(1)).findAllByUserId(1L);
            verify(orderMapper, times(1)).orderToOrderResponse(order);
            verify(orderMapper, times(1)).orderToOrderResponse(order2);
        }
    }

    @Nested
    @DisplayName("getOrderById() tests")
    class GetOrderById {
        @Test
        public void getOrderById_shouldThrowNotFound() {
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> orderService.getOrderById(1L, 1L));
        }

        @Test
        public void getOrderById_shouldReturnOrder() {
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
            when(orderMapper.orderToOrderResponse(order)).thenReturn(orderResponse);

            OrderResponse result = orderService.getOrderById(1L, 1L);

            assertEquals(orderResponse, result);
            verify(orderRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(orderMapper, times(1)).orderToOrderResponse(order);
        }
    }

    @Nested
    @DisplayName("cancelOrder() tests")
    class CancelOrder {
        @Test
        public void cancelOrder_shouldThrowBadRequest() {
            order.setOrderStatus(OrderStatus.SHIPPED);

            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

            assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L, 1L));
            verify(orderRepository, times(1)).findByIdAndUserId(1L, 1L);
        }

        @Test
        public void cancelOrder_shouldCancelOrder() {
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            orderService.cancelOrder(1L, 1L);

            ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
            assertEquals(OrderStatus.CANCELLED, orderArgumentCaptor.getValue().getOrderStatus());
        }

        @Test
        public void cancelOrder_shouldCancelOrderAndPublishEvent() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.cancelOrder(1L, ErrorCode.INSUFFICIENT_STOCK.getMessage());

            assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
            verify(orderRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(orderEventPublisher, times(1)).publishOrderCancelledEvent(any(OrderCancelledEvent.class));
        }

        @Test
        public void cancelOrder_shouldNotCancelOrderAndShouldNotPublishEvent() {
            when(orderRepository.findById(1L)).thenReturn(Optional.empty());

            orderService.cancelOrder(1L, ErrorCode.INSUFFICIENT_STOCK.getMessage());

            assertEquals(OrderStatus.CREATED, order.getOrderStatus());
            verify(orderRepository, times(1)).findById(1L);
            verifyNoMoreInteractions(orderRepository, orderEventPublisher);
        }
    }
}