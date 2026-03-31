package com.marvelous.backend.service;

import com.marvelous.backend.dto.SupplierRequest;
import com.marvelous.backend.dto.SupplierResponse;
import com.marvelous.backend.model.Supplier;
import com.marvelous.backend.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll()
                .stream()
                .map(SupplierResponse::from)
                .collect(Collectors.toList());
    }

    public SupplierResponse findById(Long id) {
        return supplierRepository.findById(id)
                .map(SupplierResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .cnpjCpf(request.getCnpjCpf())
                .contact(request.getContact())
                .productCategory(request.getProductCategory())
                .build();
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
        supplier.setName(request.getName());
        supplier.setCnpjCpf(request.getCnpjCpf());
        supplier.setContact(request.getContact());
        supplier.setProductCategory(request.getProductCategory());
        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new EntityNotFoundException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
}
