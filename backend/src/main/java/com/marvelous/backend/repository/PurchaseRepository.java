package com.marvelous.backend.repository;

import com.marvelous.backend.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByProductIdOrderByPurchaseDateDesc(Long productId);

    List<Purchase> findBySupplierId(Long supplierId);

    @Query("""
        SELECT p.supplier.id, p.supplier.name, SUM(p.unitPrice * p.quantity)
        FROM Purchase p
        GROUP BY p.supplier.id, p.supplier.name
        ORDER BY SUM(p.unitPrice * p.quantity) DESC
        """)
    List<Object[]> findTopSuppliersBySpending();

    @Query("""
        SELECT p FROM Purchase p
        WHERE p.product.id = :productId
        ORDER BY p.purchaseDate DESC, p.createdAt DESC
        LIMIT 2
        """)
    List<Purchase> findLastTwoPurchasesByProduct(@Param("productId") Long productId);
}
