package com.gustavoarantes.inventorymanagement.controller;

import com.gustavoarantes.inventorymanagement.dto.SaleRequestDTO;
import com.gustavoarantes.inventorymanagement.model.Sale;
import com.gustavoarantes.inventorymanagement.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sale> createSale(@Valid @RequestBody SaleRequestDTO saleRequest) {
        Sale newSale = saleService.registerSale(saleRequest);
        return new ResponseEntity<>(newSale, HttpStatus.CREATED);
    }
}