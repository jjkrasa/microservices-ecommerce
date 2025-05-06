package com.ecommerce.productservice.product_service.specification;

import com.ecommerce.productservice.product_service.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> hasCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> root.get("category").get("id").in(categoryIds);
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> nameOrDescriptionContains(String keyWord) {
        return (root, query, criteriaBuilder) -> {
            String[] words = keyWord.toLowerCase().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();

            for (String word : words)  {
                String likePattern = "%" + keyWord + "%";

                Predicate nameContains = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
                Predicate descriptionContains = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);

                Predicate wordMatch = criteriaBuilder.or(nameContains, descriptionContains);
                predicates.add(wordMatch);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
