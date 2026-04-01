package com.marvelous.backend.dto;

import com.marvelous.backend.model.BudgetGroup;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BudgetGroupResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private List<BudgetQuoteResponse> quotes;
    private Long bestQuoteId;

    public static BudgetGroupResponse from(BudgetGroup group) {
        BudgetGroupResponse dto = new BudgetGroupResponse();
        dto.setId(group.getId());
        dto.setTitle(group.getTitle());
        dto.setDescription(group.getDescription());
        dto.setCreatedAt(group.getCreatedAt());

        List<BudgetQuoteResponse> quoteResponses = group.getQuotes().stream()
                .map(BudgetQuoteResponse::from)
                .toList();
        dto.setQuotes(quoteResponses);

        quoteResponses.stream()
                .min((a, b) -> a.getUnitPrice().compareTo(b.getUnitPrice()))
                .ifPresent(best -> dto.setBestQuoteId(best.getId()));

        return dto;
    }
}
