package com.marvelous.backend.dto;

import com.marvelous.backend.model.Supplier;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierResponse {

    private Long id;
    private String name;
    private String cnpjCpf;
    private String contact;
    private String productCategory;
    private LocalDateTime createdAt;
    private int productCount;

    public static SupplierResponse from(Supplier supplier) {
        SupplierResponse dto = new SupplierResponse();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setCnpjCpf(supplier.getCnpjCpf());
        dto.setContact(supplier.getContact());
        dto.setProductCategory(supplier.getProductCategory());
        dto.setCreatedAt(supplier.getCreatedAt());
        dto.setProductCount(supplier.getProducts() != null ? supplier.getProducts().size() : 0);
        return dto;
    }
}
