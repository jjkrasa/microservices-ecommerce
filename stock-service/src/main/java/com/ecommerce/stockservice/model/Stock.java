package com.ecommerce.stockservice.model;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    public void reserve(int quantity) {
        if (!canReserve(quantity)) {
            throw new BadRequestException(ErrorCode.NOT_ENOUGH_STOCK_AVAILABLE.getMessage());
        }

        reservedQuantity += quantity;
    }

    private int getAvailableQuantityForReservation() {
        return availableQuantity - reservedQuantity;
    }

    private boolean canReserve(int quantity) {
        return getAvailableQuantityForReservation() >= quantity;
    }

}
