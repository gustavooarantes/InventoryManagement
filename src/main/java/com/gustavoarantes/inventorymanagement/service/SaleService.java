package com.gustavoarantes.inventorymanagement.service;

import com.gustavoarantes.inventorymanagement.dto.SaleItemDTO;
import com.gustavoarantes.inventorymanagement.dto.SaleRequestDTO;
import com.gustavoarantes.inventorymanagement.exception.InsufficientStockException;
import com.gustavoarantes.inventorymanagement.exception.ResourceNotFoundException;
import com.gustavoarantes.inventorymanagement.model.Product;
import com.gustavoarantes.inventorymanagement.model.Sale;
import com.gustavoarantes.inventorymanagement.model.SaleItem;
import com.gustavoarantes.inventorymanagement.repository.ProductRepository;
import com.gustavoarantes.inventorymanagement.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public SaleService(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Sale registerSale(SaleRequestDTO saleRequest) {
        Sale sale = new Sale();
        sale.setClientName(saleRequest.clientName());
        sale.setSellingDate(LocalDateTime.now());

        Set<SaleItem> saleItems = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleItemDTO itemDTO : saleRequest.items()) {
            Product product = productRepository.findById(itemDTO.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.productId()));

            if (product.getStockQuantity() < itemDTO.quantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemDTO.quantity());
            productRepository.save(product);

            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(itemDTO.quantity());
            saleItem.setUnitPrice(product.getPrice());
            saleItem.setSale(sale);
            saleItems.add(saleItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.quantity())));
        }

        sale.setItems(saleItems);
        sale.setTotalValue(totalAmount);

        return saleRepository.save(sale);
    }
}