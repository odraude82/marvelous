package com.marvelous.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetQuoteRequest {

    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private String notes;
}
