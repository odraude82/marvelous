package com.marvelous.backend.service;

import com.marvelous.backend.dto.SupplierRequest;
import com.marvelous.backend.dto.SupplierResponse;
import com.marvelous.backend.model.Supplier;
import com.marvelous.backend.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .name("ACME Corp")
                .cnpjCpf("12.345.678/0001-99")
                .contact("acme@example.com")
                .productCategory("Electronics")
                .build();
    }

    @Test
    void findAll_returnsAllSuppliers() {
        Supplier second = Supplier.builder().id(2L).name("Beta Ltd").build();
        when(supplierRepository.findAll()).thenReturn(List.of(supplier, second));

        List<SupplierResponse> result = supplierService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("ACME Corp");
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void findAll_emptyList_returnsEmpty() {
        when(supplierRepository.findAll()).thenReturn(List.of());

        List<SupplierResponse> result = supplierService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        SupplierResponse result = supplierService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("ACME Corp");
        assertThat(result.getCnpjCpf()).isEqualTo("12.345.678/0001-99");
        assertThat(result.getContact()).isEqualTo("acme@example.com");
        assertThat(result.getProductCategory()).isEqualTo("Electronics");
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_validRequest_returnsCreatedSupplier() {
        SupplierRequest request = new SupplierRequest();
        request.setName("New Supplier");
        request.setCnpjCpf("98.765.432/0001-11");
        request.setContact("new@example.com");
        request.setProductCategory("Food");

        Supplier saved = Supplier.builder()
                .id(3L)
                .name("New Supplier")
                .cnpjCpf("98.765.432/0001-11")
                .contact("new@example.com")
                .productCategory("Food")
                .build();
        when(supplierRepository.save(any(Supplier.class))).thenReturn(saved);

        SupplierResponse result = supplierService.create(request);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("New Supplier");
        assertThat(result.getCnpjCpf()).isEqualTo("98.765.432/0001-11");
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void create_minimalRequest_savesWithoutOptionalFields() {
        SupplierRequest request = new SupplierRequest();
        request.setName("Minimal Supplier");

        Supplier saved = Supplier.builder().id(4L).name("Minimal Supplier").build();
        when(supplierRepository.save(any(Supplier.class))).thenReturn(saved);

        SupplierResponse result = supplierService.create(request);

        assertThat(result.getName()).isEqualTo("Minimal Supplier");
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void update_existingId_updatesAllFields() {
        SupplierRequest request = new SupplierRequest();
        request.setName("Updated Corp");
        request.setCnpjCpf("00.111.222/0001-33");
        request.setContact("updated@example.com");
        request.setProductCategory("Machinery");

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> inv.getArgument(0));

        SupplierResponse result = supplierService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Updated Corp");
        assertThat(result.getCnpjCpf()).isEqualTo("00.111.222/0001-33");
        assertThat(result.getContact()).isEqualTo("updated@example.com");
        assertThat(result.getProductCategory()).isEqualTo("Machinery");
        verify(supplierRepository).save(supplier);
    }

    @Test
    void update_nonExistingId_throwsEntityNotFoundException() {
        SupplierRequest request = new SupplierRequest();
        request.setName("X");
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.update(99L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(supplierRepository, never()).save(any());
    }

    @Test
    void delete_existingId_deletesSupplier() {
        when(supplierRepository.existsById(1L)).thenReturn(true);

        supplierService.delete(1L);

        verify(supplierRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsEntityNotFoundException() {
        when(supplierRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> supplierService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(supplierRepository, never()).deleteById(any());
    }
}
