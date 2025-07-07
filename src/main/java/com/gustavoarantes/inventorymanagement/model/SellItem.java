package com.gustavoarantes.inventorymanagement.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "selling_items")
public class SellItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selling_id", nullable = false) // Chave estrangeira para a tabela 'vendas'.
    @ToString.Exclude
    private Sell sell;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantiti;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
