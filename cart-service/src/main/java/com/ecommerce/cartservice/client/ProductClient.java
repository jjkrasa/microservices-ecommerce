package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", path = "/api/products")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductResponse getProductById(@PathVariable Long id);

    @GetMapping ("/batch")
    @CollectionFormat(feign.CollectionFormat.CSV)
    List<ProductResponse> getProductsByIds(@RequestParam("ids") List<Long> ids);
}
