package com.ecommerce.stockservice.controller;

import com.ecommerce.stockservice.dto.CreateStockRequest;
import com.ecommerce.stockservice.dto.ReserveStockRequest;
import com.ecommerce.stockservice.dto.StockResponse;
import com.ecommerce.stockservice.dto.UpdateStockRequest;
import com.ecommerce.stockservice.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{productId}")
    public ResponseEntity<StockResponse> getStockByProductId(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(stockService.getStockByProductId(productId));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<StockResponse>> getStocksByProductIds(@RequestParam("productIds") List<Long> productIds) {
        return ResponseEntity.ok(stockService.getStocksByProductIds(productIds));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<StockResponse> createStock(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody CreateStockRequest createStockRequest
    ) {
        StockResponse stock = stockService.createStock(productId, createStockRequest);

        return new ResponseEntity<>(stock, HttpStatus.CREATED);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Void> updateStockQuantity(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateStockRequest updateStockRequest
    ) {
        stockService.updateStockQuantity(productId, updateStockRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{productId}/reserve")
    public ResponseEntity<Void> reserveStockByProductId(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ReserveStockRequest reserveStockRequest
    ) {
        stockService.reserveStockByProductId(productId, reserveStockRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
