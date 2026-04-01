package com.marvelous.backend.service;

import com.marvelous.backend.dto.DashboardResponse;
import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Purchase;
import com.marvelous.backend.model.Supplier;
import com.marvelous.backend.repository.ProductRepository;
import com.marvelous.backend.repository.PurchaseRepository;
import com.marvelous.backend.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboard_noData_returnsZeroedDashboard() {
        when(productRepository.findAll()).thenReturn(List.of());
        when(productRepository.findLowStockProducts()).thenReturn(List.of());
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(supplierRepository.count()).thenReturn(0L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getTotalStockValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTotalProducts()).isZero();
        assertThat(result.getTotalSuppliers()).isZero();
        assertThat(result.getLowStockCount()).isZero();
        assertThat(result.getLowStockItems()).isEmpty();
        assertThat(result.getTopSuppliers()).isEmpty();
        assertThat(result.getRecentPriceVariations()).isEmpty();
    }

    @Test
    void getDashboard_withProducts_calculatesTotalStockValue() {
        Product p1 = Product.builder().id(1L).name("A").costPrice(new BigDecimal("10.00"))
                .stockQuantity(5).unitOfMeasure("un").minStockAlert(2).build();
        Product p2 = Product.builder().id(2L).name("B").costPrice(new BigDecimal("20.00"))
                .stockQuantity(3).unitOfMeasure("un").minStockAlert(1).build();

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));
        when(productRepository.findLowStockProducts()).thenReturn(List.of());
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(purchaseRepository.findLastTwoPurchasesByProduct(anyLong())).thenReturn(List.of());
        when(supplierRepository.count()).thenReturn(2L);

        DashboardResponse result = dashboardService.getDashboard();

        // 10*5 + 20*3 = 50 + 60 = 110
        assertThat(result.getTotalStockValue()).isEqualByComparingTo("110.00");
        assertThat(result.getTotalProducts()).isEqualTo(2);
        assertThat(result.getTotalSuppliers()).isEqualTo(2);
    }

    @Test
    void getDashboard_withLowStockProducts_returnsLowStockItems() {
        Supplier supplier = Supplier.builder().id(5L).name("Vendor").build();
        Product low = Product.builder().id(3L).name("Low Product").costPrice(BigDecimal.ONE)
                .stockQuantity(1).unitOfMeasure("kg").minStockAlert(5).supplier(supplier).build();

        when(productRepository.findAll()).thenReturn(List.of(low));
        when(productRepository.findLowStockProducts()).thenReturn(List.of(low));
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(purchaseRepository.findLastTwoPurchasesByProduct(3L)).thenReturn(List.of());
        when(supplierRepository.count()).thenReturn(1L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getLowStockCount()).isEqualTo(1);
        assertThat(result.getLowStockItems()).hasSize(1);

        DashboardResponse.LowStockItem item = result.getLowStockItems().get(0);
        assertThat(item.getProductId()).isEqualTo(3L);
        assertThat(item.getProductName()).isEqualTo("Low Product");
        assertThat(item.getCurrentStock()).isEqualTo(1);
        assertThat(item.getMinStock()).isEqualTo(5);
        assertThat(item.getUnitOfMeasure()).isEqualTo("kg");
        assertThat(item.getSupplierName()).isEqualTo("Vendor");
    }

    @Test
    void getDashboard_lowStockProductWithNoSupplier_showsNA() {
        Product low = Product.builder().id(4L).name("No Supplier").costPrice(BigDecimal.ONE)
                .stockQuantity(0).unitOfMeasure("un").minStockAlert(5).build();

        when(productRepository.findAll()).thenReturn(List.of(low));
        when(productRepository.findLowStockProducts()).thenReturn(List.of(low));
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(purchaseRepository.findLastTwoPurchasesByProduct(4L)).thenReturn(List.of());
        when(supplierRepository.count()).thenReturn(0L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getLowStockItems().get(0).getSupplierName()).isEqualTo("N/A");
    }

    @Test
    void getDashboard_withTopSuppliers_mapsSupplierSpending() {
        Object[] row = {5L, "Top Vendor", new BigDecimal("500.00")};
        List<Object[]> supplierRows = new java.util.ArrayList<>();
        supplierRows.add(row);
        when(productRepository.findAll()).thenReturn(List.of());
        when(productRepository.findLowStockProducts()).thenReturn(List.of());
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(supplierRows);
        when(supplierRepository.count()).thenReturn(1L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getTopSuppliers()).hasSize(1);
        DashboardResponse.SupplierSpending spending = result.getTopSuppliers().get(0);
        assertThat(spending.getSupplierId()).isEqualTo(5L);
        assertThat(spending.getSupplierName()).isEqualTo("Top Vendor");
        assertThat(spending.getTotalSpent()).isEqualByComparingTo("500.00");
    }

    @Test
    void getDashboard_withTwoPurchasesForProduct_calculatesPriceVariation() {
        Product product = Product.builder().id(1L).name("Widget").costPrice(new BigDecimal("12.00"))
                .stockQuantity(10).unitOfMeasure("un").minStockAlert(2).build();

        Supplier supplier = Supplier.builder().id(5L).name("Supplier A").build();
        Purchase recent = Purchase.builder().id(1L).product(product).supplier(supplier)
                .quantity(5).unitPrice(new BigDecimal("12.00"))
                .purchaseDate(LocalDate.of(2024, 3, 1)).build();
        Purchase previous = Purchase.builder().id(2L).product(product).supplier(supplier)
                .quantity(5).unitPrice(new BigDecimal("10.00"))
                .purchaseDate(LocalDate.of(2024, 1, 1)).build();

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productRepository.findLowStockProducts()).thenReturn(List.of());
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(purchaseRepository.findLastTwoPurchasesByProduct(1L)).thenReturn(List.of(recent, previous));
        when(supplierRepository.count()).thenReturn(1L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getRecentPriceVariations()).hasSize(1);
        DashboardResponse.PriceVariation variation = result.getRecentPriceVariations().get(0);
        assertThat(variation.getProductId()).isEqualTo(1L);
        assertThat(variation.getProductName()).isEqualTo("Widget");
        assertThat(variation.getPreviousPrice()).isEqualByComparingTo("10.00");
        assertThat(variation.getCurrentPrice()).isEqualByComparingTo("12.00");
        // (12 - 10) / 10 * 100 = 20%
        assertThat(variation.getVariationPercent()).isEqualByComparingTo("20.00");
        assertThat(variation.getSupplierName()).isEqualTo("Supplier A");
    }

    @Test
    void getDashboard_priceVariation_previousPriceZero_variationIsZero() {
        Product product = Product.builder().id(1L).name("Widget").costPrice(new BigDecimal("5.00"))
                .stockQuantity(10).unitOfMeasure("un").minStockAlert(2).build();

        Supplier supplier = Supplier.builder().id(5L).name("Vendor").build();
        Purchase recent = Purchase.builder().id(1L).product(product).supplier(supplier)
                .quantity(5).unitPrice(new BigDecimal("5.00"))
                .purchaseDate(LocalDate.now()).build();
        Purchase previous = Purchase.builder().id(2L).product(product).supplier(supplier)
                .quantity(5).unitPrice(BigDecimal.ZERO)
                .purchaseDate(LocalDate.now().minusDays(1)).build();

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productRepository.findLowStockProducts()).thenReturn(List.of());
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(purchaseRepository.findLastTwoPurchasesByProduct(1L)).thenReturn(List.of(recent, previous));
        when(supplierRepository.count()).thenReturn(0L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getRecentPriceVariations().get(0).getVariationPercent())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getDashboard_onlyOnePurchaseForProduct_noVariationEntry() {
        Product product = Product.builder().id(1L).name("Single Purchase").costPrice(BigDecimal.TEN)
                .stockQuantity(10).unitOfMeasure("un").minStockAlert(2).build();

        Purchase onePurchase = Purchase.builder().id(1L).product(product)
                .quantity(1).unitPrice(BigDecimal.TEN).purchaseDate(LocalDate.now()).build();

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productRepository.findLowStockProducts()).thenReturn(List.of());
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(purchaseRepository.findLastTwoPurchasesByProduct(1L)).thenReturn(List.of(onePurchase));
        when(supplierRepository.count()).thenReturn(0L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getRecentPriceVariations()).isEmpty();
    }

    @Test
    void getDashboard_purchaseWithNullSupplier_showsNA() {
        Product product = Product.builder().id(1L).name("Widget").costPrice(BigDecimal.TEN)
                .stockQuantity(10).unitOfMeasure("un").minStockAlert(2).build();

        Purchase recent = Purchase.builder().id(1L).product(product)
                .quantity(5).unitPrice(new BigDecimal("12.00"))
                .purchaseDate(LocalDate.now()).build();
        Purchase previous = Purchase.builder().id(2L).product(product)
                .quantity(5).unitPrice(new BigDecimal("10.00"))
                .purchaseDate(LocalDate.now().minusDays(1)).build();

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productRepository.findLowStockProducts()).thenReturn(List.of());
        when(purchaseRepository.findTopSuppliersBySpending()).thenReturn(List.of());
        when(purchaseRepository.findLastTwoPurchasesByProduct(1L)).thenReturn(List.of(recent, previous));
        when(supplierRepository.count()).thenReturn(0L);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getRecentPriceVariations().get(0).getSupplierName()).isEqualTo("N/A");
    }
}
