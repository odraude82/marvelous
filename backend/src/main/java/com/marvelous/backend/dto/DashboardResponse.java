package com.marvelous.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private BigDecimal totalStockValue;
    private int totalProducts;
    private int totalSuppliers;
    private int lowStockCount;
    private List<LowStockItem> lowStockItems;
    private List<SupplierSpending> topSuppliers;
    private List<PriceVariation> recentPriceVariations;

    @Data
    @Builder
    public static class LowStockItem {
        private Long productId;
        private String productName;
        private int currentStock;
        private int minStock;
        private String unitOfMeasure;
        private String supplierName;
    }

    @Data
    @Builder
    public static class SupplierSpending {
        private Long supplierId;
        private String supplierName;
        private BigDecimal totalSpent;
    }

    @Data
    @Builder
    public static class PriceVariation {
        private Long productId;
        private String productName;
        private BigDecimal previousPrice;
        private BigDecimal currentPrice;
        private BigDecimal variationPercent;
        private String supplierName;
    }
}
