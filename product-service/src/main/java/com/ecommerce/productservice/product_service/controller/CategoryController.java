package com.ecommerce.productservice.product_service.controller;

import com.ecommerce.productservice.product_service.dto.CategoryResponse;
import com.ecommerce.productservice.product_service.dto.CreateCategoryRequest;
import com.ecommerce.productservice.product_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @PostMapping("/admin")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CreateCategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);

        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/{categoryId}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategoryById(categoryId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
