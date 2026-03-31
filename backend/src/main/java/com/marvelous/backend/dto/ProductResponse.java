package com.marvelous.backend.dto;

import com.marvelous.backend.model.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private String unitOfMeasure;
    private Integer minStockAlert;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime createdAt;
    private BigDecimal totalValue;
    private boolean lowStock;

    public static ProductResponse from(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCostPrice(product.getCostPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setUnitOfMeasure(product.getUnitOfMeasure());
        dto.setMinStockAlert(product.getMinStockAlert());
        dto.setCreatedAt(product.getCreatedAt());
        if (product.getSupplier() != null) {
            dto.setSupplierId(product.getSupplier().getId());
            dto.setSupplierName(product.getSupplier().getName());
        }
        dto.setTotalValue(product.getCostPrice().multiply(BigDecimal.valueOf(product.getStockQuantity())));
        dto.setLowStock(product.getStockQuantity() <= product.getMinStockAlert());
        return dto;
    }
}
