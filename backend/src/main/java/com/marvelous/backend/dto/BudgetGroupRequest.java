package com.marvelous.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BudgetGroupRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
