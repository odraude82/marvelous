package com.marvelous.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvelous.backend.dto.ProductRequest;
import com.marvelous.backend.dto.ProductResponse;
import com.marvelous.backend.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Widget");
        productResponse.setCostPrice(new BigDecimal("9.99"));
        productResponse.setStockQuantity(100);
        productResponse.setUnitOfMeasure("un");
        productResponse.setMinStockAlert(5);
        productResponse.setTotalValue(new BigDecimal("999.00"));
        productResponse.setLowStock(false);
    }

    @Test
    void findAll_returnsListOfProducts() throws Exception {
        ProductResponse second = new ProductResponse();
        second.setId(2L);
        second.setName("Gadget");
        second.setCostPrice(BigDecimal.TEN);
        second.setStockQuantity(50);
        when(productService.findAll()).thenReturn(List.of(productResponse, second));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Widget"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void findAll_empty_returnsEmptyArray() throws Exception {
        when(productService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findById_existingId_returnsProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Widget"))
                .andExpect(jsonPath("$.costPrice").value(9.99))
                .andExpect(jsonPath("$.stockQuantity").value(100))
                .andExpect(jsonPath("$.lowStock").value(false));
    }

    @Test
    void findById_nonExistingId_returnsNotFound() throws Exception {
        when(productService.findById(99L))
                .thenThrow(new EntityNotFoundException("Product not found with id: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("99")));
    }

    @Test
    void findBySupplierId_returnsProductsForSupplier() throws Exception {
        when(productService.findBySupplierId(10L)).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products/supplier/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void findLowStock_returnsLowStockProducts() throws Exception {
        productResponse.setLowStock(true);
        when(productService.findLowStock()).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lowStock").value(true));
    }

    @Test
    void create_validRequest_returnsCreatedProduct() throws Exception {
        ProductRequest request = buildRequest("New Widget", new BigDecimal("5.00"), 50, "un");

        when(productService.create(any(ProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_blankName_returnsBadRequest() throws Exception {
        ProductRequest request = buildRequest("", new BigDecimal("5.00"), 50, "un");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_nullCostPrice_returnsBadRequest() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Widget");
        request.setCostPrice(null);
        request.setStockQuantity(10);
        request.setUnitOfMeasure("un");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_negativeCostPrice_returnsBadRequest() throws Exception {
        ProductRequest request = buildRequest("Widget", new BigDecimal("-1.00"), 50, "un");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_negativeStockQuantity_returnsBadRequest() throws Exception {
        ProductRequest request = buildRequest("Widget", new BigDecimal("5.00"), -1, "un");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existingId_returnsUpdatedProduct() throws Exception {
        ProductRequest request = buildRequest("Updated Widget", new BigDecimal("15.00"), 200, "m");

        when(productService.update(eq(1L), any(ProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_nonExistingId_returnsNotFound() throws Exception {
        ProductRequest request = buildRequest("X", BigDecimal.ONE, 1, "un");

        when(productService.update(eq(99L), any(ProductRequest.class)))
                .thenThrow(new EntityNotFoundException("Product not found with id: 99"));

        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingId_returnsNoContent() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).delete(1L);
    }

    @Test
    void delete_nonExistingId_returnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Product not found with id: 99"))
                .when(productService).delete(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    private ProductRequest buildRequest(String name, BigDecimal costPrice, Integer stock, String unit) {
        ProductRequest r = new ProductRequest();
        r.setName(name);
        r.setCostPrice(costPrice);
        r.setStockQuantity(stock);
        r.setUnitOfMeasure(unit);
        return r;
    }
}
