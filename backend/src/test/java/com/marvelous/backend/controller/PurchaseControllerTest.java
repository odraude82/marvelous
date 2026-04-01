package com.marvelous.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvelous.backend.dto.PurchaseRequest;
import com.marvelous.backend.dto.PurchaseResponse;
import com.marvelous.backend.service.PurchaseService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PurchaseService purchaseService;

    private PurchaseResponse purchaseResponse;

    @BeforeEach
    void setUp() {
        purchaseResponse = new PurchaseResponse();
        purchaseResponse.setId(100L);
        purchaseResponse.setProductId(1L);
        purchaseResponse.setProductName("Widget");
        purchaseResponse.setSupplierId(10L);
        purchaseResponse.setSupplierName("ACME Corp");
        purchaseResponse.setQuantity(20);
        purchaseResponse.setUnitPrice(new BigDecimal("4.50"));
        purchaseResponse.setTotalAmount(new BigDecimal("90.00"));
        purchaseResponse.setPurchaseDate(LocalDate.of(2024, 1, 15));
        purchaseResponse.setNotes("Bulk order");
    }

    @Test
    void findAll_returnsListOfPurchases() throws Exception {
        PurchaseResponse second = new PurchaseResponse();
        second.setId(200L);
        when(purchaseService.findAll()).thenReturn(List.of(purchaseResponse, second));

        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].productName").value("Widget"))
                .andExpect(jsonPath("$[1].id").value(200));
    }

    @Test
    void findAll_empty_returnsEmptyArray() throws Exception {
        when(purchaseService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findById_existingId_returnsPurchase() throws Exception {
        when(purchaseService.findById(100L)).thenReturn(purchaseResponse);

        mockMvc.perform(get("/api/purchases/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.supplierId").value(10))
                .andExpect(jsonPath("$.quantity").value(20))
                .andExpect(jsonPath("$.unitPrice").value(4.50))
                .andExpect(jsonPath("$.totalAmount").value(90.00))
                .andExpect(jsonPath("$.notes").value("Bulk order"));
    }

    @Test
    void findById_nonExistingId_returnsNotFound() throws Exception {
        when(purchaseService.findById(999L))
                .thenThrow(new EntityNotFoundException("Purchase not found with id: 999"));

        mockMvc.perform(get("/api/purchases/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("999")));
    }

    @Test
    void findByProductId_returnsPurchasesForProduct() throws Exception {
        when(purchaseService.findByProductId(1L)).thenReturn(List.of(purchaseResponse));

        mockMvc.perform(get("/api/purchases/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productId").value(1));
    }

    @Test
    void create_validRequest_returnsCreatedPurchase() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setSupplierId(10L);
        request.setQuantity(20);
        request.setUnitPrice(new BigDecimal("4.50"));
        request.setPurchaseDate(LocalDate.of(2024, 1, 15));

        when(purchaseService.create(any(PurchaseRequest.class))).thenReturn(purchaseResponse);

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void create_nullProductId_returnsBadRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(null);
        request.setSupplierId(10L);
        request.setQuantity(1);
        request.setUnitPrice(BigDecimal.ONE);

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_nullSupplierId_returnsBadRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setSupplierId(null);
        request.setQuantity(1);
        request.setUnitPrice(BigDecimal.ONE);

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_zeroQuantity_returnsBadRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setSupplierId(10L);
        request.setQuantity(0);
        request.setUnitPrice(BigDecimal.ONE);

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_zeroUnitPrice_returnsBadRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setSupplierId(10L);
        request.setQuantity(1);
        request.setUnitPrice(BigDecimal.ZERO);

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_productNotFound_returnsNotFound() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(999L);
        request.setSupplierId(10L);
        request.setQuantity(1);
        request.setUnitPrice(BigDecimal.ONE);

        when(purchaseService.create(any(PurchaseRequest.class)))
                .thenThrow(new EntityNotFoundException("Product not found with id: 999"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingId_returnsNoContent() throws Exception {
        doNothing().when(purchaseService).delete(100L);

        mockMvc.perform(delete("/api/purchases/100"))
                .andExpect(status().isNoContent());

        verify(purchaseService).delete(100L);
    }

    @Test
    void delete_nonExistingId_returnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Purchase not found with id: 999"))
                .when(purchaseService).delete(999L);

        mockMvc.perform(delete("/api/purchases/999"))
                .andExpect(status().isNotFound());
    }
}
