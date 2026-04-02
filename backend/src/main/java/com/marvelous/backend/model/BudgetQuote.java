package com.marvelous.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_quotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_group_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BudgetGroup budgetGroup;

    @NotBlank
    @Column(nullable = false)
    private String supplierName;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Positive
    private Integer quantity;

    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
