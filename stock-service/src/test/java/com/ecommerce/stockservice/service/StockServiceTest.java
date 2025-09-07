package com.ecommerce.stockservice.service;

import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.NotFoundException;
import com.ecommerce.stockservice.dto.*;
import com.ecommerce.stockservice.mapper.StockMapper;
import com.ecommerce.stockservice.model.Stock;
import com.ecommerce.stockservice.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockMapper stockMapper;

    private Stock stock;

    private StockResponse stockResponse;

    @BeforeEach
    void setUp() {
        stock = new Stock(1L, 5, 0);
        stockResponse = new StockResponse(1L, 5);
    }

    @Nested
    @DisplayName("getStockByProductId")
    class GetStockByProductId {
        @Test
        public void getStockByProductId_shouldThrowNotFound() {
            when(stockRepository.findById(2L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> stockService.getStockByProductId(2L));
            verify(stockRepository, times(1)).findById(2L);
        }

        @Test
        public void getStockByProductId_shouldReturnStockDto() {
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
            when(stockMapper.stockToStockResponse(stock)).thenReturn(stockResponse);

            StockResponse response = stockService.getStockByProductId(1L);

            assertEquals(stockResponse, response);
            verify(stockRepository, times(1)).findById(1L);
            verify(stockMapper, times(1)).stockToStockResponse(stock);
        }
    }

    @Nested
    @DisplayName("getStocksByProductIds")
    class GetStocksByProductIds {
        @Test
        public void getStocksByProductIds_shouldReturnStocksDto() {
            Stock stock2 = new Stock(2L, 6, 1);
            StockResponse stockResponse2 = new StockResponse(2L, 6);


            when(stockRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(stock, stock2));
            when(stockMapper.stockToStockResponse(stock)).thenReturn(stockResponse);
            when(stockMapper.stockToStockResponse(stock2)).thenReturn(stockResponse2);

            List<StockResponse> result = stockService.getStocksByProductIds(List.of(1L, 2L));

            assertIterableEquals(List.of(stockResponse, stockResponse2), result);
            verify(stockRepository, times(1)).findAllById(List.of(1L, 2L));
            verify(stockMapper, times(1)).stockToStockResponse(stock);
            verify(stockMapper, times(1)).stockToStockResponse(stock2);
        }
    }

    @Nested
    @DisplayName("createStock() tests")
    class CreateStock {
        @Test
        public void createStock_shouldCreateStockAndReturnStockDto() {
            CreateStockRequest req = new CreateStockRequest(5);

            when(stockMapper.createStockRequestToStock(1L, req)).thenReturn(stock);
            when(stockMapper.stockToStockResponse(stock)).thenReturn(stockResponse);

            StockResponse result = stockService.createStock(1L, req);

            assertEquals(stockResponse, result);
            verify(stockMapper, times(1)).createStockRequestToStock(1L, req);
            verify(stockRepository, times(1)).save(stock);
            verify(stockMapper, times(1)).stockToStockResponse(stock);
        }
    }

    @Nested
    @DisplayName("updateStockQuantity() tests")
    class UpdateStockQuantity {
        @Test
        public void updateStockQuantity_shouldThrowBadRequest() {
            UpdateStockRequest updateStockRequest = new UpdateStockRequest(-6);

            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            assertThrows(BadRequestException.class, () -> stockService.updateStockQuantity(1L, updateStockRequest));
            verify(stockRepository, times(1)).findById(1L);
        }

        @Test
        public void updateStockQuantity_shouldUpdateStockWithNegativeRequest() {
            UpdateStockRequest updateStockRequest = new UpdateStockRequest(-5);

            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            stockService.updateStockQuantity(1L, updateStockRequest);

            assertEquals(0, stock.getAvailableQuantity());
            verify(stockRepository, times(1)).findById(1L);
            verify(stockRepository, times(1)).save(stock);
        }

        @Test
        public void updateStockQuantity_shouldUpdateStockWithPositiveRequest() {
            UpdateStockRequest updateStockRequest = new UpdateStockRequest(5);

            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            stockService.updateStockQuantity(1L, updateStockRequest);

            assertEquals(10, stock.getAvailableQuantity());
            verify(stockRepository, times(1)).findById(1L);
            verify(stockRepository, times(1)).save(stock);
        }
    }

    @Nested
    @DisplayName("reserveStockByProductId() tests")
    class ReserveStockByProductId {
        @Test
        public void reserveStockByProductId_shouldThrowBadRequest() {
            ReserveStockRequest reserveStockRequest = new ReserveStockRequest(6);

            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            assertThrows(BadRequestException.class, () -> stockService.reserveStockByProductId(1L, reserveStockRequest));
            verify(stockRepository, times(1)).findById(1L);
        }

        @Test
        public void reserveStockByProductId_shouldReserveStock() {
            ReserveStockRequest reserveStockRequest = new ReserveStockRequest(5);

            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            stockService.reserveStockByProductId(1L, reserveStockRequest);

            assertEquals(5, stock.getReservedQuantity());
            verify(stockRepository, times(1)).findById(1L);
            verify(stockRepository, times(1)).save(stock);
        }
    }

    @Nested
    @DisplayName("reserveAllOrThrow() tests")
    class ReserveAllOrThrow {
        @Test
        public void reserveAllOrThrow_shouldUpdateReservedStock() {
            Stock stock1 = new Stock(1L, 10, 2);
            Stock stock2 = new Stock(2L, 15, 5);

            StockReserveRequestedEvent.ReserveItem item1 = new StockReserveRequestedEvent.ReserveItem(1L, 3);
            StockReserveRequestedEvent.ReserveItem item2 = new StockReserveRequestedEvent.ReserveItem(2L, 4);
            StockReserveRequestedEvent event = new StockReserveRequestedEvent(1L, List.of(item1, item2));

            when(stockRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(stock1, stock2));

            stockService.reserveAllOrThrow(event);

            assertEquals(5, stock1.getReservedQuantity());
            assertEquals(9, stock2.getReservedQuantity());
            verify(stockRepository, times(1)).findAllById(List.of(1L, 2L));

        }

        @Test
        public void reserveAllOrThrow_shouldThrowBadRequest() {
            Stock stock1 = new Stock(1L, 5, 1);
            StockReserveRequestedEvent.ReserveItem item1 = new StockReserveRequestedEvent.ReserveItem(1L, 9);
            StockReserveRequestedEvent event = new StockReserveRequestedEvent(100L, List.of(item1));

            when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock1));

            assertThrows(BadRequestException.class, () -> stockService.reserveAllOrThrow(event));
            verify(stockRepository, times(1)).findAllById(List.of(1L));
        }
    }
}