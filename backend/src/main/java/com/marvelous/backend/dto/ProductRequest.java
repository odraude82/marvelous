package com.marvelous.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Cost price is required")
    @Positive(message = "Cost price must be positive")
    private BigDecimal costPrice;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;

    @NotBlank(message = "Unit of measure is required")
    private String unitOfMeasure;

    private Integer minStockAlert = 5;

    private Long supplierId;
}
