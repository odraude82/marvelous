package com.marvelous.backend.dto;

import com.marvelous.backend.model.Purchase;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Long supplierId;
    private String supplierName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private LocalDate purchaseDate;
    private String notes;
    private LocalDateTime createdAt;

    public static PurchaseResponse from(Purchase purchase) {
        PurchaseResponse dto = new PurchaseResponse();
        dto.setId(purchase.getId());
        dto.setQuantity(purchase.getQuantity());
        dto.setUnitPrice(purchase.getUnitPrice());
        dto.setTotalAmount(purchase.getUnitPrice().multiply(BigDecimal.valueOf(purchase.getQuantity())));
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setNotes(purchase.getNotes());
        dto.setCreatedAt(purchase.getCreatedAt());
        if (purchase.getProduct() != null) {
            dto.setProductId(purchase.getProduct().getId());
            dto.setProductName(purchase.getProduct().getName());
        }
        if (purchase.getSupplier() != null) {
            dto.setSupplierId(purchase.getSupplier().getId());
            dto.setSupplierName(purchase.getSupplier().getName());
        }
        return dto;
    }
}
