package com.ecommerce.productservice.product_service.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean hasPreviousPage,
        boolean hasNextPage
) {
}
