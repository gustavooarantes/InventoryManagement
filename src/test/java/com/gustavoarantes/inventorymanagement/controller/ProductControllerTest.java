package com.gustavoarantes.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavoarantes.inventorymanagement.dto.ProductDTO;
import com.gustavoarantes.inventorymanagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void whenListAllProducts_shouldReturnProductList() throws Exception {
        ProductDTO productDTO = new ProductDTO(1L, "Test", "Desc", BigDecimal.TEN, 10, 1);
        given(productService.listAll()).willReturn(Collections.singletonList(productDTO));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenCreateProductAsAdmin_shouldReturnCreated() throws Exception {
        ProductDTO inputDTO = new ProductDTO(null, "New Product", "New", BigDecimal.ONE, 1, 1);
        ProductDTO outputDTO = new ProductDTO(1L, "New Product", "New", BigDecimal.ONE, 1, 1);
        given(productService.createProduct(any(ProductDTO.class))).willReturn(outputDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }
}