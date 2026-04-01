package com.marvelous.backend.service;

import com.marvelous.backend.dto.BudgetGroupRequest;
import com.marvelous.backend.dto.BudgetGroupResponse;
import com.marvelous.backend.dto.BudgetQuoteRequest;
import com.marvelous.backend.dto.BudgetQuoteResponse;
import com.marvelous.backend.model.BudgetGroup;
import com.marvelous.backend.model.BudgetQuote;
import com.marvelous.backend.repository.BudgetGroupRepository;
import com.marvelous.backend.repository.BudgetQuoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetGroupRepository budgetGroupRepository;
    private final BudgetQuoteRepository budgetQuoteRepository;

    public List<BudgetGroupResponse> findAll() {
        return budgetGroupRepository.findAllWithQuotes().stream()
                .map(BudgetGroupResponse::from)
                .toList();
    }

    public BudgetGroupResponse findById(Long id) {
        BudgetGroup group = budgetGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget group not found with id: " + id));
        return BudgetGroupResponse.from(group);
    }

    @Transactional
    public BudgetGroupResponse create(BudgetGroupRequest request) {
        BudgetGroup group = BudgetGroup.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        return BudgetGroupResponse.from(budgetGroupRepository.save(group));
    }

    @Transactional
    public BudgetGroupResponse update(Long id, BudgetGroupRequest request) {
        BudgetGroup group = budgetGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget group not found with id: " + id));
        group.setTitle(request.getTitle());
        group.setDescription(request.getDescription());
        return BudgetGroupResponse.from(budgetGroupRepository.save(group));
    }

    @Transactional
    public void delete(Long id) {
        if (!budgetGroupRepository.existsById(id)) {
            throw new EntityNotFoundException("Budget group not found with id: " + id);
        }
        budgetGroupRepository.deleteById(id);
    }

    @Transactional
    public BudgetQuoteResponse addQuote(Long groupId, BudgetQuoteRequest request) {
        BudgetGroup group = budgetGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Budget group not found with id: " + groupId));
        BudgetQuote quote = BudgetQuote.builder()
                .budgetGroup(group)
                .supplierName(request.getSupplierName())
                .unitPrice(request.getUnitPrice())
                .quantity(request.getQuantity())
                .notes(request.getNotes())
                .build();
        return BudgetQuoteResponse.from(budgetQuoteRepository.save(quote));
    }

    @Transactional
    public void deleteQuote(Long quoteId) {
        if (!budgetQuoteRepository.existsById(quoteId)) {
            throw new EntityNotFoundException("Budget quote not found with id: " + quoteId);
        }
        budgetQuoteRepository.deleteById(quoteId);
    }
}
