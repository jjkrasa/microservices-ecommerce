package com.ecommerce.orderservice.order_service.controller;

import com.ecommerce.orderservice.order_service.dto.OrderResponse;
import com.ecommerce.orderservice.order_service.service.OrderService;
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
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader("X-User-Id") Long userId) {
        OrderResponse order = orderService.createOrder(userId);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUsersOrders(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(orderService.getUsersOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@RequestHeader("X-User-Id") Long userId, @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(userId, orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestHeader("X-User-Id") Long userId, @PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }
}
