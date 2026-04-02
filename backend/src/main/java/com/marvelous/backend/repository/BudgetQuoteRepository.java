package com.marvelous.backend.repository;

import com.marvelous.backend.model.BudgetQuote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetQuoteRepository extends JpaRepository<BudgetQuote, Long> {

    List<BudgetQuote> findByBudgetGroupIdOrderByUnitPriceAsc(Long budgetGroupId);
}
