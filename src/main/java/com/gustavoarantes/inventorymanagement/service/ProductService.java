package com.gustavoarantes.inventorymanagement.service;

import com.gustavoarantes.inventorymanagement.dto.ProductDTO;
import com.gustavoarantes.inventorymanagement.exception.ResourceNotFoundException;
import com.gustavoarantes.inventorymanagement.model.Product;
import com.gustavoarantes.inventorymanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private ProductDTO toDto(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCriticalStockLimit()
        );
    }

    private void mapDtoToEntity(ProductDTO productDTO, Product product) {
        product.setName(productDTO.name());
        product.setDescription(productDTO.description());
        product.setPrice(productDTO.price());
        product.setStockQuantity(productDTO.stockQuantity());
        product.setCriticalStockLimit(productDTO.criticalStockLimit());
    }

    @Transactional(readOnly = true)
    @Cacheable("products")
    public List<ProductDTO> listAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found."));
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();

        mapDtoToEntity(productDTO, product);

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "products", key = "#id")
    })
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found."));

        mapDtoToEntity(productDTO, product);

        Product updatedProduct = productRepository.save(product);
        return toDto(updatedProduct);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "products", key = "#id")
    })
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id " + id + " not found.");
        }
        productRepository.deleteById(id);
    }
}