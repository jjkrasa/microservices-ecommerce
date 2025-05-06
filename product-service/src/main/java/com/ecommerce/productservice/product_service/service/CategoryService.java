package com.ecommerce.productservice.product_service.service;

import com.ecommerce.productservice.product_service.dto.CategoryResponse;
import com.ecommerce.productservice.product_service.dto.CreateCategoryRequest;
import com.ecommerce.productservice.product_service.exception.ConflictException;
import com.ecommerce.productservice.product_service.exception.ErrorCode;
import com.ecommerce.productservice.product_service.exception.NotFoundException;
import com.ecommerce.productservice.product_service.mapper.CategoryMapper;
import com.ecommerce.productservice.product_service.model.Category;
import com.ecommerce.productservice.product_service.repository.CategoryRepository;
import com.ecommerce.productservice.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    private final CategoryMapper categoryMapper;

    public Category getCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND.getMessage()));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categoryMapper.categoriesToCategoriesResponse(categories);
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = categoryMapper.createCategoryRequestToCategory(request);

        categoryRepository.save(category);

        return categoryMapper.categoryToCategoryResponse(category);
    }

    @Transactional
    public void deleteCategoryById(Long categoryId) {
        getCategoryByIdOrThrow(categoryId);

        if (productRepository.existsByCategory_Id(categoryId)) {
            throw new ConflictException(ErrorCode.CATEGORY_IN_USE.getMessage());
        }

        categoryRepository.deleteById(categoryId);
    }
}
