package com.ecommerce.orderservice.controller;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.orderservice.dto.CreateOrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid CreateOrderRequest createOrderRequest
    ) {
        OrderResponse order = orderService.createOrder(userId, null, createOrderRequest);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUsersOrders(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(orderService.getUsersOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(userId, orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("orderId") Long orderId
    ) {
        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/anonymous")
    public ResponseEntity<OrderResponse> createAnonymousOrder(
            @CookieValue(name = "sessionId", required = false) String sessionId,
            @RequestBody @Valid CreateOrderRequest createOrderRequest
    ) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new BadRequestException(ErrorCode.MISSING_USER_OR_SESSION.getMessage());
        }

        OrderResponse order = orderService.createOrder(null, sessionId, createOrderRequest);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
