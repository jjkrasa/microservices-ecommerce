package com.ecommerce.productservice.product_service.service;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.productservice.product_service.client.StockClient;
import com.ecommerce.productservice.product_service.dto.*;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ImageService imageService;

    private final CategoryService categoryService;

    private final ProductMapper productMapper;

    private final StockClient stockClient;

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = getProductByIdOrThrow(productId);
        ProductResponse productResponse = productMapper.productToProductResponse(product);

        StockResponse stock = stockClient.getStockByProductId(productId);

        productResponse.setAvailableQuantity(stock.availableQuantity());

        return productResponse;
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(
            Pageable pageable,
            Specification<Product> specification
    ) {
        Page<Product> productsPage = productRepository.findAll(specification, pageable);

        List<Long> productIds = productsPage.getContent().stream().map(Product::getId).toList();

        List<StockResponse> stocks = stockClient.getStocksByProductIds(productIds);
        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(StockResponse::productId, StockResponse::availableQuantity));

        List<ProductResponse> productResponse = productsPage
                .getContent()
                .stream()
                .map(product -> {
                    Integer availableQuantity = stockMap.getOrDefault(product.getId(), 0);

                    return productMapper.productToProductResponse(product, availableQuantity);
                })
                .toList();

        return new PagedResponse<>(
                productResponse,
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.hasPrevious(),
                productsPage.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByIds(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        List<StockResponse> stocks = stockClient.getStocksByProductIds(productIds);
        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(StockResponse::productId, StockResponse::availableQuantity));

        return products
                .stream()
                .map(product -> {
                    Integer availableQuantity = stockMap.getOrDefault(product.getId(), 0);

                    return productMapper.productToProductResponse(product, availableQuantity);
                })
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryService.getCategoryByIdOrThrow(request.getCategoryId());

        Product product = productMapper.createProductRequestToProduct(request);
        product.setCategory(category);

        productRepository.save(product);

        stockClient.createStock(product.getId(), new CreateStockRequest(request.getAvailableQuantity()));

        return productMapper.productToProductResponse(product, request.getAvailableQuantity());
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
