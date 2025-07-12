package com.ecommerce.productservice.mapper;

import com.ecommerce.productservice.dto.CreateProductRequest;
import com.ecommerce.productservice.dto.PagedResponse;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.dto.UpdateProductRequest;
import com.ecommerce.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "availableQuantity", ignore = true)
    @Mapping(target = "imageUrl", source = "imageUrl")
    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "category", source = "product.category.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "availableQuantity", source = "availableQuantity")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    ProductResponse productToProductResponse(Product product, Integer availableQuantity);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "imageUrl", source = "imageUrl")
    Product createProductRequestToProduct(CreateProductRequest request);

    void updateProductFromUpdateProductRequest(UpdateProductRequest request, @MappingTarget Product product);

    default PagedResponse<ProductResponse> pageToPagedResponse(Page<Product> productsPage) {
        List<ProductResponse> content = productsPage
                .getContent()
                .stream()
                .map(this::productToProductResponse)
                .toList();

        return new PagedResponse<>(
                content,
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.hasPrevious(),
                productsPage.hasNext()
        );
    }
}
