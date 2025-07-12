package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.*;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ImageService;
import com.ecommerce.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ImageService imageService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getProducts(@Valid ProductFilterRequest filterRequest) {
        Pageable pageable = filterRequest.toPageable();
        Specification<Product> specification = filterRequest.toSpecification();

        return ResponseEntity.ok(productService.getAllProducts(pageable, specification));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<ProductResponse>> getProductsByIds(@RequestParam("ids") List<Long> productIds) {
        return ResponseEntity.ok(productService.getProductsByIds(productIds));
    }

    @PostMapping("/admin")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse product = productService.createProduct(request);

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping("/admin/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable("productId") Long productId, @Valid @RequestBody UpdateProductRequest request) {
        productService.updateProduct(productId, request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/admin/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("productId") Long productId) {
        productService.deleteProductById(productId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/admin/images")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(imageService.saveImage(file));
    }

    @PatchMapping("/admin/{productId}/images")
    public ResponseEntity<String> updateProductImage(
            @PathVariable("productId") Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(productService.updateProductImage(productId, file));
    }
}
