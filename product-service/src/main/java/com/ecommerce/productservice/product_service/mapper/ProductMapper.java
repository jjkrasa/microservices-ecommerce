package com.ecommerce.productservice.product_service.mapper;

import com.ecommerce.productservice.product_service.dto.CreateProductRequest;
import com.ecommerce.productservice.product_service.dto.PagedResponse;
import com.ecommerce.productservice.product_service.dto.ProductResponse;
import com.ecommerce.productservice.product_service.dto.UpdateProductRequest;
import com.ecommerce.productservice.product_service.model.Product;
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
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "imageUrl", source = "imageUrl")
    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "quantity", source = "quantity")
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
