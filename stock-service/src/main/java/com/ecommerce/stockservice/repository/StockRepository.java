package com.ecommerce.stockservice.repository;

import com.ecommerce.stockservice.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
