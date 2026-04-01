package com.marvelous.backend.repository;

import com.marvelous.backend.model.BudgetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BudgetGroupRepository extends JpaRepository<BudgetGroup, Long> {

    @Query("SELECT DISTINCT bg FROM BudgetGroup bg LEFT JOIN FETCH bg.quotes ORDER BY bg.createdAt DESC")
    List<BudgetGroup> findAllWithQuotes();
}
