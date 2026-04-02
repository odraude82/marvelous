package com.marvelous.backend.controller;

import com.marvelous.backend.dto.BudgetGroupRequest;
import com.marvelous.backend.dto.BudgetGroupResponse;
import com.marvelous.backend.dto.BudgetQuoteRequest;
import com.marvelous.backend.dto.BudgetQuoteResponse;
import com.marvelous.backend.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetGroupResponse>> findAll() {
        return ResponseEntity.ok(budgetService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetGroupResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BudgetGroupResponse> create(@Valid @RequestBody BudgetGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetGroupResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody BudgetGroupRequest request) {
        return ResponseEntity.ok(budgetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/quotes")
    public ResponseEntity<BudgetQuoteResponse> addQuote(@PathVariable Long id,
                                                         @Valid @RequestBody BudgetQuoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.addQuote(id, request));
    }

    @DeleteMapping("/quotes/{quoteId}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long quoteId) {
        budgetService.deleteQuote(quoteId);
        return ResponseEntity.noContent().build();
    }
}
