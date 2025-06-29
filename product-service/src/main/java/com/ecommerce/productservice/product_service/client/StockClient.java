package com.ecommerce.productservice.product_service.client;

import com.ecommerce.productservice.product_service.dto.CreateStockRequest;
import com.ecommerce.productservice.product_service.dto.StockResponse;
import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "stock-service", path = "/api/stocks")
public interface StockClient {

    @GetMapping("/{productId}")
    StockResponse getStockByProductId(@PathVariable("productId") Long productId);

    @GetMapping("/batch")
    @CollectionFormat(feign.CollectionFormat.CSV)
    List<StockResponse> getStocksByProductIds(@RequestParam("productIds") List<Long> productIds);

    @PostMapping("/{productId}")
    StockResponse createStock(@PathVariable("productId") Long productId, @RequestBody CreateStockRequest createStockRequest);
}
