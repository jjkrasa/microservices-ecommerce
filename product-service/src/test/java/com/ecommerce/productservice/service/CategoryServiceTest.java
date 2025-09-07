package com.ecommerce.productservice.service;

import com.ecommerce.exceptionlib.exception.ConflictException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.productservice.dto.CategoryResponse;
import com.ecommerce.productservice.dto.CreateCategoryRequest;
import com.ecommerce.productservice.mapper.CategoryMapper;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private Category category;

    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Category");
        categoryResponse = new CategoryResponse(1L, "Category");
    }

    @Nested
    @DisplayName("getCategoryByIdOrThrow() tests")
    class GetCategoryByIdOrThrowTests {
        @Test
        public void getCategoryByIdOrThrow_shouldThrowNotFound() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () ->  categoryService.getCategoryByIdOrThrow(1L));
            verify(categoryRepository, times(1)).findById(1L);
        }

        @Test
        public void getCategoryByIdOrThrow_shouldReturnCategory() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            Category result = categoryService.getCategoryByIdOrThrow(1L);

            assertEquals(category, result);
            verify(categoryRepository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("getCategories() tests")
    class GetCategories {
        @Test
        public void getCategories_shouldReturnNoCategories() {
            when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
            when(categoryMapper.categoriesToCategoriesResponse(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<CategoryResponse> result = categoryService.getCategories();

            assertEquals(0, result.size());
            verify(categoryRepository, times(1)).findAll();
            verify(categoryMapper, times(1)).categoriesToCategoriesResponse(Collections.emptyList());
        }

        @Test
        public void getCategories_shouldTwoCategories() {
            Category category2 = new Category(2L, "Category2");
            CategoryResponse categoryResponse2 = new CategoryResponse(2L, "Category2");

            List<Category> categoryList = List.of(category, category2);
            List<CategoryResponse> categoryResponseList = List.of(categoryResponse, categoryResponse2);

            when(categoryRepository.findAll()).thenReturn(categoryList);

            when(categoryMapper.categoriesToCategoriesResponse(categoryList)).thenReturn(categoryResponseList);

            List<CategoryResponse> result = categoryService.getCategories();

            assertIterableEquals(categoryResponseList, result);
            verify(categoryRepository, times(1)).findAll();
            verify(categoryMapper, times(1)).categoriesToCategoriesResponse(categoryList);
        }
    }

    @Nested
    @DisplayName("createCategory() tests")
    class CreateCategory {
        @Test
        public void createCategory_shouldCreateCategory() {
            CreateCategoryRequest request = new CreateCategoryRequest();
            request.setName("CreatedCategory");

            Category categoryToSave = new Category(null, "CreatedCategory");
            CategoryResponse expectedCategoryResponse = new CategoryResponse(2L, "CreatedCategory");

            when(categoryMapper.createCategoryRequestToCategory(request)).thenReturn(categoryToSave);
            when(categoryMapper.categoryToCategoryResponse(categoryToSave)).thenReturn(expectedCategoryResponse);

            CategoryResponse result = categoryService.createCategory(request);
            assertEquals(expectedCategoryResponse, result);
            verify(categoryMapper, times(1)).createCategoryRequestToCategory(request);
            verify(categoryRepository, times(1)).save(categoryToSave);
            verify(categoryMapper, times(1)).categoryToCategoryResponse(categoryToSave);
        }
    }

    @Nested
    @DisplayName("deleteCategoryById() tests")
    class DeleteCategoryById {
        @Test
        public void deleteCategoryById_shouldThrowConflict() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productRepository.existsByCategory_Id(1L)).thenReturn(true);

            assertThrows(ConflictException.class, () -> categoryService.deleteCategoryById(1L));
            verify(categoryRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).existsByCategory_Id(1L);
        }

        @Test
        public void deleteCategoryById_shouldDeleteCategory() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productRepository.existsByCategory_Id(1L)).thenReturn(false);

            categoryService.deleteCategoryById(1L);

            verify(categoryRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).existsByCategory_Id(1L);
            verify(categoryRepository).deleteById(1L);
        }
    }
}