package com.marvelous.backend.dto;

import com.marvelous.backend.model.BudgetQuote;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetQuoteResponse {

    private Long id;
    private Long budgetGroupId;
    private String supplierName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String notes;
    private LocalDateTime createdAt;

    public static BudgetQuoteResponse from(BudgetQuote quote) {
        BudgetQuoteResponse dto = new BudgetQuoteResponse();
        dto.setId(quote.getId());
        dto.setSupplierName(quote.getSupplierName());
        dto.setUnitPrice(quote.getUnitPrice());
        dto.setQuantity(quote.getQuantity());
        dto.setNotes(quote.getNotes());
        dto.setCreatedAt(quote.getCreatedAt());
        if (quote.getBudgetGroup() != null) {
            dto.setBudgetGroupId(quote.getBudgetGroup().getId());
        }
        return dto;
    }
}
