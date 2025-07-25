package com.gustavoarantes.inventorymanagement.repository;

import com.gustavoarantes.inventorymanagement.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

}