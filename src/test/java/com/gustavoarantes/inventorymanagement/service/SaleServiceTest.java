package com.gustavoarantes.inventorymanagement.service;

import com.gustavoarantes.inventorymanagement.dto.SaleItemDTO;
import com.gustavoarantes.inventorymanagement.dto.SaleRequestDTO;
import com.gustavoarantes.inventorymanagement.exception.InsufficientStockException;
import com.gustavoarantes.inventorymanagement.model.Product;
import com.gustavoarantes.inventorymanagement.model.Sale;
import com.gustavoarantes.inventorymanagement.repository.ProductRepository;
import com.gustavoarantes.inventorymanagement.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Queue queue;

    @InjectMocks
    private SaleService saleService;

    @Test
    void shouldRegisterSaleAndDecreaseStockSuccessfully() {

        Product product = new Product(1L, "Test Product", "Desc", new BigDecimal("10.00"), 100, 10);
        SaleRequestDTO saleRequest = new SaleRequestDTO("Test Client", Collections.singletonList(new SaleItemDTO(1L, 5)));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sale result = saleService.registerSale(saleRequest);

        assertNotNull(result);
        assertEquals(95, product.getStockQuantity());
        assertEquals(new BigDecimal("50.00"), result.getTotalValue());
        verify(productRepository, times(1)).save(product);
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    void shouldThrowInsufficientStockExceptionWhenStockIsTooLow() {

        Product product = new Product(1L, "Test Product", "Desc", new BigDecimal("10.00"), 5, 10);
        SaleRequestDTO saleRequest = new SaleRequestDTO("Test Client", Collections.singletonList(new SaleItemDTO(1L, 10)));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> {
            saleService.registerSale(saleRequest);
        });

        assertEquals(5, product.getStockQuantity());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void shouldTriggerRabbitMQNotificationOnCriticalStock() {

        Product product = new Product(1L, "Test Product", "Desc", new BigDecimal("10.00"), 12, 10);
        SaleRequestDTO saleRequest = new SaleRequestDTO("Test Client", Collections.singletonList(new SaleItemDTO(1L, 3)));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(queue.getName()).thenReturn("critical-stock-alerts");

        saleService.registerSale(saleRequest);

        assertEquals(9, product.getStockQuantity());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("critical-stock-alerts"), anyString());
    }
}