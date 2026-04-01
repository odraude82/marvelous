package com.marvelous.backend.controller;

import com.marvelous.backend.dto.DashboardResponse;
import com.marvelous.backend.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void getDashboard_returnsFullDashboard() throws Exception {
        DashboardResponse.LowStockItem lowItem = DashboardResponse.LowStockItem.builder()
                .productId(3L).productName("Low Product").currentStock(1).minStock(5)
                .unitOfMeasure("kg").supplierName("Vendor").build();

        DashboardResponse.SupplierSpending spending = DashboardResponse.SupplierSpending.builder()
                .supplierId(10L).supplierName("Top Vendor").totalSpent(new BigDecimal("500.00")).build();

        DashboardResponse.PriceVariation variation = DashboardResponse.PriceVariation.builder()
                .productId(1L).productName("Widget").previousPrice(new BigDecimal("10.00"))
                .currentPrice(new BigDecimal("12.00")).variationPercent(new BigDecimal("20.00"))
                .supplierName("Supplier A").build();

        DashboardResponse response = DashboardResponse.builder()
                .totalStockValue(new BigDecimal("1500.00"))
                .totalProducts(10)
                .totalSuppliers(3)
                .lowStockCount(1)
                .lowStockItems(List.of(lowItem))
                .topSuppliers(List.of(spending))
                .recentPriceVariations(List.of(variation))
                .build();

        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStockValue").value(1500.00))
                .andExpect(jsonPath("$.totalProducts").value(10))
                .andExpect(jsonPath("$.totalSuppliers").value(3))
                .andExpect(jsonPath("$.lowStockCount").value(1))
                .andExpect(jsonPath("$.lowStockItems", hasSize(1)))
                .andExpect(jsonPath("$.lowStockItems[0].productName").value("Low Product"))
                .andExpect(jsonPath("$.topSuppliers", hasSize(1)))
                .andExpect(jsonPath("$.topSuppliers[0].supplierName").value("Top Vendor"))
                .andExpect(jsonPath("$.recentPriceVariations", hasSize(1)))
                .andExpect(jsonPath("$.recentPriceVariations[0].variationPercent").value(20.00));
    }

    @Test
    void getDashboard_emptyData_returnsZeroedResponse() throws Exception {
        DashboardResponse response = DashboardResponse.builder()
                .totalStockValue(BigDecimal.ZERO)
                .totalProducts(0)
                .totalSuppliers(0)
                .lowStockCount(0)
                .lowStockItems(List.of())
                .topSuppliers(List.of())
                .recentPriceVariations(List.of())
                .build();

        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(0))
                .andExpect(jsonPath("$.lowStockItems", hasSize(0)))
                .andExpect(jsonPath("$.topSuppliers", hasSize(0)));
    }
}
