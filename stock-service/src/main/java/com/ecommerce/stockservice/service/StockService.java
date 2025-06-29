package com.ecommerce.stockservice.service;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.stockservice.dto.*;
import com.ecommerce.stockservice.mapper.StockMapper;
import com.ecommerce.stockservice.model.Stock;
import com.ecommerce.stockservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    private final StockMapper stockMapper;

    @Transactional(readOnly = true)
    public StockResponse getStockByProductId(Long productId) {
        Stock stock = findStockByProductIdOrThrow(productId);

        return stockMapper.stockToStockResponse(stock);
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByProductIds(List<Long> productIds) {
        List<Stock> stocks = stockRepository.findAllById(productIds);

        return stocks
                .stream()
                .map(stockMapper::stockToStockResponse)
                .toList();
    }

    @Transactional
    public StockResponse createStock(
            Long productId,
            CreateStockRequest createStockRequest
    ) {
        Stock stock = stockMapper.createStockRequestToStock(productId, createStockRequest);

        stockRepository.save(stock);

        return stockMapper.stockToStockResponse(stock);
    }

    @Transactional
    public void updateStockQuantity(Long productId, UpdateStockRequest updateStockRequest) {
        Stock stock = findStockByProductIdOrThrow(productId);

        int updatedAvailableQuantity = stock.getAvailableQuantity() + updateStockRequest.getQuantityChange();

        if (updatedAvailableQuantity < 0) {
            throw new BadRequestException(ErrorCode.STOCK_CANT_BE_NEGATIVE.getMessage());
        }

        stock.setAvailableQuantity(updatedAvailableQuantity);
        stockRepository.save(stock);
    }

    @Transactional
    public void reserveStockByProductId(Long productId, ReserveStockRequest reserveStockRequest) {
        Stock stock = findStockByProductIdOrThrow(productId);

        if (stock.getAvailableQuantity() - stock.getReservedQuantity() < reserveStockRequest.getQuantity()) {
            throw new BadRequestException(ErrorCode.NOT_ENOUGH_STOCK_AVAILABLE.getMessage());
        }

        stock.setReservedQuantity(stock.getReservedQuantity() + reserveStockRequest.getQuantity());
        stockRepository.save(stock);
    }

    @Transactional
    public void reserveAllOrThrow(StockReserveRequestedEvent reserveEvent) {
        Map<Long, Integer> itemsQuantityByIdMap = reserveEvent.getItems()
                .stream()
                .collect(
                        Collectors.toMap(
                                StockReserveRequestedEvent.ReserveItem::getProductId,
                                StockReserveRequestedEvent.ReserveItem::getQuantity
                        )
                );

        List<Long> productIds = reserveEvent.getItems().stream().map(StockReserveRequestedEvent.ReserveItem::getProductId).toList();
        List<Stock> stocks = stockRepository.findAllById(productIds);


        for (Stock stock : stocks) {
            int requestedItemQuantityToReserve = itemsQuantityByIdMap.get(stock.getProductId());

            stock.reserve(requestedItemQuantityToReserve);
        }
    }

    private Stock findStockByProductIdOrThrow(Long productId) {
        return stockRepository.findById(productId).orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }
}
