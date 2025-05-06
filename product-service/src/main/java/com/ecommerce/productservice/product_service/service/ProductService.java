package com.ecommerce.productservice.product_service.service;

import com.ecommerce.productservice.product_service.dto.CreateProductRequest;
import com.ecommerce.productservice.product_service.dto.PagedResponse;
import com.ecommerce.productservice.product_service.dto.ProductResponse;
import com.ecommerce.productservice.product_service.dto.UpdateProductRequest;
import com.ecommerce.productservice.product_service.exception.ErrorCode;
import com.ecommerce.productservice.product_service.exception.NotFoundException;
import com.ecommerce.productservice.product_service.mapper.ProductMapper;
import com.ecommerce.productservice.product_service.model.Category;
import com.ecommerce.productservice.product_service.model.Product;
import com.ecommerce.productservice.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ImageService imageService;

    private final CategoryService categoryService;

    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = getProductByIdOrThrow(productId);

        return productMapper.productToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(
            Pageable pageable,
            Specification<Product> specification
    ) {
        Page<Product> productsPage = productRepository.findAll(specification, pageable);

        return productMapper.pageToPagedResponse(productsPage);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByIds(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        return products
                .stream()
                .map(productMapper::productToProductResponse)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryService.getCategoryByIdOrThrow(request.getCategoryId());

        Product product = productMapper.createProductRequestToProduct(request);
        product.setCategory(category);

        productRepository.save(product);

        return productMapper.productToProductResponse(product);
    }

    @Transactional
    public void updateProduct(Long productId, UpdateProductRequest request) {
        Product product = getProductByIdOrThrow(productId);
        Category category = categoryService.getCategoryByIdOrThrow(request.getCategoryId());

        productMapper.updateProductFromUpdateProductRequest(request, product);
        product.setCategory(category);

        productRepository.save(product);
    }

    @Transactional
    public void deleteProductById(Long productId) {
        Product product = getProductByIdOrThrow(productId);

        imageService.deleteImage(product.getImageUrl());
        productRepository.deleteById(productId);
    }

    @Transactional
    public String updateProductImage(Long productId, MultipartFile file) {
        Product product = getProductByIdOrThrow(productId);

        String newImageUrl = imageService.replaceProductImage(product.getImageUrl(), file);

        product.setImageUrl(newImageUrl);
        productRepository.save(product);

        return newImageUrl;
    }

    private Product getProductByIdOrThrow(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }
}
