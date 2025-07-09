package com.gustavoarantes.inventorymanagement.dto;

import java.util.List;

public record SaleRequestDTO(String clientName, List<SaleItemDTO> items) {
}
