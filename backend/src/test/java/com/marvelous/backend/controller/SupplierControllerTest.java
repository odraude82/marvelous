package com.marvelous.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvelous.backend.dto.SupplierRequest;
import com.marvelous.backend.dto.SupplierResponse;
import com.marvelous.backend.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SupplierService supplierService;

    private SupplierResponse supplierResponse;

    @BeforeEach
    void setUp() {
        supplierResponse = new SupplierResponse();
        supplierResponse.setId(1L);
        supplierResponse.setName("ACME Corp");
        supplierResponse.setCnpjCpf("12.345.678/0001-99");
        supplierResponse.setContact("acme@example.com");
        supplierResponse.setProductCategory("Electronics");
        supplierResponse.setProductCount(3);
    }

    @Test
    void findAll_returnsListOfSuppliers() throws Exception {
        SupplierResponse second = new SupplierResponse();
        second.setId(2L);
        second.setName("Beta Ltd");
        when(supplierService.findAll()).thenReturn(List.of(supplierResponse, second));

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("ACME Corp"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void findAll_empty_returnsEmptyArray() throws Exception {
        when(supplierService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findById_existingId_returnsSupplier() throws Exception {
        when(supplierService.findById(1L)).thenReturn(supplierResponse);

        mockMvc.perform(get("/api/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ACME Corp"))
                .andExpect(jsonPath("$.cnpjCpf").value("12.345.678/0001-99"))
                .andExpect(jsonPath("$.productCount").value(3));
    }

    @Test
    void findById_nonExistingId_returnsNotFound() throws Exception {
        when(supplierService.findById(99L)).thenThrow(new EntityNotFoundException("Supplier not found with id: 99"));

        mockMvc.perform(get("/api/suppliers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("99")));
    }

    @Test
    void create_validRequest_returnsCreatedSupplier() throws Exception {
        SupplierRequest request = new SupplierRequest();
        request.setName("New Supplier");
        request.setCnpjCpf("98.765.432/0001-11");
        request.setContact("new@example.com");
        request.setProductCategory("Food");

        when(supplierService.create(any(SupplierRequest.class))).thenReturn(supplierResponse);

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_missingName_returnsBadRequest() throws Exception {
        SupplierRequest request = new SupplierRequest();
        // name is intentionally omitted (blank)
        request.setName("");

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existingId_returnsUpdatedSupplier() throws Exception {
        SupplierRequest request = new SupplierRequest();
        request.setName("Updated Corp");

        when(supplierService.update(eq(1L), any(SupplierRequest.class))).thenReturn(supplierResponse);

        mockMvc.perform(put("/api/suppliers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_nonExistingId_returnsNotFound() throws Exception {
        SupplierRequest request = new SupplierRequest();
        request.setName("X");

        when(supplierService.update(eq(99L), any(SupplierRequest.class)))
                .thenThrow(new EntityNotFoundException("Supplier not found with id: 99"));

        mockMvc.perform(put("/api/suppliers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingId_returnsNoContent() throws Exception {
        doNothing().when(supplierService).delete(1L);

        mockMvc.perform(delete("/api/suppliers/1"))
                .andExpect(status().isNoContent());

        verify(supplierService).delete(1L);
    }

    @Test
    void delete_nonExistingId_returnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Supplier not found with id: 99"))
                .when(supplierService).delete(99L);

        mockMvc.perform(delete("/api/suppliers/99"))
                .andExpect(status().isNotFound());
    }
}
