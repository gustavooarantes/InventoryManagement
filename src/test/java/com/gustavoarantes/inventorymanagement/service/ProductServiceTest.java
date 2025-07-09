package com.gustavoarantes.inventorymanagement.service;

import com.gustavoarantes.inventorymanagement.dto.ProductDTO;
import com.gustavoarantes.inventorymanagement.model.Product;
import com.gustavoarantes.inventorymanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Test Product", "Description", new BigDecimal("10.00"), 100, 10);
        productDTO = new ProductDTO(1L, "Test Product", "Description", new BigDecimal("10.00"), 100, 10);
    }

    @Test
    void whenFindById_shouldReturnProductDTO() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO foundProduct = productService.findById(1L);

        assertNotNull(foundProduct);
        assertEquals(product.getName(), foundProduct.name());
        verify(productRepository).findById(1L); // Verify that the repository method was called
    }

    @Test
    void whenCreateProduct_shouldReturnSavedProductDTO() {

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO createdProduct = productService.createProduct(productDTO);

        assertNotNull(createdProduct);
        assertEquals("Test Product", createdProduct.name());
        verify(productRepository).save(any(Product.class));
    }
}