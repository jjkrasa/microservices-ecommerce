package com.ecommerce.productservice.client;

import com.ecommerce.productservice.dto.CreateStockRequest;
import com.ecommerce.productservice.dto.StockResponse;
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
