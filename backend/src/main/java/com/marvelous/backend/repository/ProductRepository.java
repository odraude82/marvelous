package com.marvelous.backend.repository;

import com.marvelous.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySupplierId(Long supplierId);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStockAlert")
    List<Product> findLowStockProducts();
}
