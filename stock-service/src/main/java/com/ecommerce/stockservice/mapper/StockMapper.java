package com.ecommerce.stockservice.mapper;

import com.ecommerce.stockservice.dto.CreateStockRequest;
import com.ecommerce.stockservice.dto.StockResponse;
import com.ecommerce.stockservice.model.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "availableQuantity", source = "availableQuantity")
    StockResponse stockToStockResponse(Stock stock);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "availableQuantity", source = "createStockRequest.availableQuantity")
    @Mapping(target = "reservedQuantity", constant = "0")
    Stock createStockRequestToStock(Long productId, CreateStockRequest createStockRequest);
}
