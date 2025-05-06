package com.ecommerce.productservice.product_service.mapper;

import com.ecommerce.productservice.product_service.dto.CategoryResponse;
import com.ecommerce.productservice.product_service.dto.CreateCategoryRequest;
import com.ecommerce.productservice.product_service.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "name", source = "name")
    Category createCategoryRequestToCategory(CreateCategoryRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryResponse categoryToCategoryResponse(Category category);

    default List<CategoryResponse> categoriesToCategoriesResponse(List<Category> categories) {
        return categories
                .stream()
                .map(this::categoryToCategoryResponse)
                .toList();
    }
}
