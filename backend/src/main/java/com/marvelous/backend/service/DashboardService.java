package com.marvelous.backend.service;

import com.marvelous.backend.dto.DashboardResponse;
import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Purchase;
import com.marvelous.backend.repository.ProductRepository;
import com.marvelous.backend.repository.PurchaseRepository;
import com.marvelous.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseRepository purchaseRepository;

    public DashboardResponse getDashboard() {
        List<Product> allProducts = productRepository.findAll();

        // Total stock value
        BigDecimal totalStockValue = allProducts.stream()
                .map(p -> p.getCostPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Low stock items
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        List<DashboardResponse.LowStockItem> lowStockItems = lowStockProducts.stream()
                .map(p -> DashboardResponse.LowStockItem.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .currentStock(p.getStockQuantity())
                        .minStock(p.getMinStockAlert())
                        .unitOfMeasure(p.getUnitOfMeasure())
                        .supplierName(p.getSupplier() != null ? p.getSupplier().getName() : "N/A")
                        .build())
                .toList();

        // Top suppliers by spending
        List<Object[]> supplierData = purchaseRepository.findTopSuppliersBySpending();
        List<DashboardResponse.SupplierSpending> topSuppliers = supplierData.stream()
                .map(row -> DashboardResponse.SupplierSpending.builder()
                        .supplierId(((Number) row[0]).longValue())
                        .supplierName((String) row[1])
                        .totalSpent(new BigDecimal(row[2].toString()))
                        .build())
                .toList();

        // Price variation for recently purchased products
        List<DashboardResponse.PriceVariation> priceVariations = new ArrayList<>();
        for (Product product : allProducts) {
            List<Purchase> lastTwo = purchaseRepository.findLastTwoPurchasesByProduct(product.getId());
            if (lastTwo.size() == 2) {
                BigDecimal current = lastTwo.get(0).getUnitPrice();
                BigDecimal previous = lastTwo.get(1).getUnitPrice();
                BigDecimal variation = BigDecimal.ZERO;
                if (previous.compareTo(BigDecimal.ZERO) != 0) {
                    variation = current.subtract(previous)
                            .divide(previous, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
                }
                priceVariations.add(DashboardResponse.PriceVariation.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .previousPrice(previous)
                        .currentPrice(current)
                        .variationPercent(variation)
                        .supplierName(lastTwo.get(0).getSupplier() != null
                                ? lastTwo.get(0).getSupplier().getName() : "N/A")
                        .build());
            }
        }

        return DashboardResponse.builder()
                .totalStockValue(totalStockValue)
                .totalProducts(allProducts.size())
                .totalSuppliers((int) supplierRepository.count())
                .lowStockCount(lowStockItems.size())
                .lowStockItems(lowStockItems)
                .topSuppliers(topSuppliers)
                .recentPriceVariations(priceVariations)
                .build();
    }
}
