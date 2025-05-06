package com.ecommerce.productservice.product_service.dto;

import com.ecommerce.productservice.product_service.model.Product;
import com.ecommerce.productservice.product_service.specification.ProductSpecifications;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductFilterRequest {

    private Integer pageNumber = 0;

    private Integer pageSize = 10;

    private List<Long> categoryIds;

    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
    private BigDecimal maxPrice;

    private String keyWord;

    private String sortBy = "id";

    private String sortDirection = "asc";

    @AssertTrue(message = "Minimum price must be less than or equal to maximum price")
    public boolean isPriceValid() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }

        return minPrice.compareTo(maxPrice) <= 0;
    }

    public Pageable toPageable()  {
        return PageRequest.of(
                pageNumber,
                pageSize,
                sortDirection.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending()
        );
    }

    public Specification<Product> toSpecification() {
        Specification<Product> specification = Specification.where(null);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            specification = specification.and(ProductSpecifications.hasCategories(categoryIds));
        }
        if (minPrice != null) {
            specification = specification.and(ProductSpecifications.hasMinPrice(minPrice));
        }
        if (maxPrice != null) {
            specification = specification.and(ProductSpecifications.hasMaxPrice(maxPrice));
        }
        if (keyWord != null && !keyWord.isBlank()) {
            specification = specification.and(ProductSpecifications.nameOrDescriptionContains(keyWord));
        }

        return specification;
    }
}
