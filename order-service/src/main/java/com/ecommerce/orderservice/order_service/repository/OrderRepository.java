package com.ecommerce.orderservice.order_service.repository;

import com.ecommerce.orderservice.order_service.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    @EntityGraph(attributePaths = "items")
    List<Order> findAllByUserId(Long userId);
}
