package com.gustavoarantes.inventorymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,

        @NotBlank(message = "Name cannot be blank.")
        @Size(min = 3, max = 100, message = "Name should have at least 3 and a maximum of 100 letters.")
        String name,

        String description,

        @NotNull(message = "The price cannot be null.")
        @Positive(message = "The price should be positive.")
        BigDecimal price,

        @NotNull(message = "The stock quantity cannot be null.")
        @Positive(message = "The stock quantity should be positive.")
        Integer stockQuantity,

        @NotNull(message = "The limit cannot be null.")
        @Positive(message = "The limit should be positive.")
        Integer criticalStockLimit
) {}
