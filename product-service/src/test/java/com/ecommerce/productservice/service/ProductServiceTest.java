package com.ecommerce.productservice.service;

import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.productservice.client.StockClient;
import com.ecommerce.productservice.dto.*;
import com.ecommerce.productservice.mapper.ProductMapper;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private StockClient stockClient;

    private Product product;

    private Category category;

    private ProductResponse productResponse;

    private StockResponse stockResponse;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Category");
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category(category)
                .description("Test Description")
                .price(BigDecimal.valueOf(100.0))
                .imageUrl("/images/test.jpg")
                .build();
        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .category("Category")
                .description("Test Description")
                .price(BigDecimal.valueOf(100.0))
                .availableQuantity(10)
                .imageUrl("/images/test.jpg")
                .build();
        stockResponse = new StockResponse(1L, 10);
    }

    @Test
    void getProductById_shouldReturnProductResponse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.productToProductResponse(product)).thenReturn(productResponse);
        when(stockClient.getStockByProductId(1L)).thenReturn(stockResponse);

        ProductResponse response = productService.getProductById(1L);

        assertEquals(1L, response.getId());
        assertEquals(stockResponse.availableQuantity(), response.getAvailableQuantity());
    }

    @Test
    void getProductById_shouldThrowNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getAllProducts_shouldReturnPagedResponse() {
        List<Product> products = List.of(product);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageRequest, 1);

        when(productRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);
        when(stockClient.getStocksByProductIds(anyList())).thenReturn(List.of(stockResponse));
        when(productMapper.productToProductResponse(any(Product.class), anyInt())).thenReturn(productResponse);

        PagedResponse<ProductResponse> result = productService.getAllProducts(pageRequest, Specification.where(null));

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        assertFalse(result.hasPreviousPage());
        assertFalse(result.hasNextPage());

        verify(productRepository).findAll(any(Specification.class), eq(pageRequest));
        verify(stockClient).getStocksByProductIds(List.of(product.getId()));
        verify(productMapper).productToProductResponse(eq(product), eq(stockResponse.availableQuantity()));
    }

    @Test
    void getProductsByIds_shouldReturnList() {
        when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
        when(stockClient.getStocksByProductIds(anyList())).thenReturn(List.of(stockResponse));
        when(productMapper.productToProductResponse(any(Product.class), anyInt())).thenReturn(productResponse);

        List<ProductResponse> result = productService.getProductsByIds(List.of(1L));

        assertEquals(1, result.size());
    }

    @Test
    void createProduct_shouldCreateProduct() {
        CreateProductRequest req = new CreateProductRequest();
        req.setCategoryId(1L);
        req.setAvailableQuantity(10);

        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(category);
        when(productMapper.createProductRequestToProduct(req)).thenReturn(product);
        when(productMapper.productToProductResponse(product, 10)).thenReturn(productResponse);

        ProductResponse response = productService.createProduct(req);

        assertNotNull(response);
        assertEquals(req.getCategoryId(), product.getCategory().getId());
        verify(stockClient).createStock(eq(product.getId()), any(CreateStockRequest.class));
    }

    @Test
    void updateProduct_shouldUpdateProduct() {
        UpdateProductRequest req = new UpdateProductRequest();
        req.setName("Updated Name");
        req.setCategoryId(1L);
        req.setDescription("Updated Description");
        req.setPrice(BigDecimal.valueOf(150.0));
        req.setImageUrl("/images/test.jpg");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(category);

        productService.updateProduct(1L, req);

        verify(productRepository).save(product);
    }

    @Test
    void deleteProductById_shouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProductById(1L);

        verify(imageService).deleteImage(product.getImageUrl());
        verify(productRepository).deleteById(1L);
    }

    @Test
    void updateProductImage_shouldUpdateImage() {
        MultipartFile file = mock(MultipartFile.class);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(imageService.replaceProductImage(anyString(), eq(file))).thenReturn("/images/new.jpg");

        String url = productService.updateProductImage(1L, file);

        assertEquals("/images/new.jpg", url);
        verify(productRepository).save(product);
    }
}
