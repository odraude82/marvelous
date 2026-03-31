package com.marvelous.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String cnpjCpf;

    private String contact;

    private String productCategory;
}
