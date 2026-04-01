package com.marvelous.backend.service;

import com.marvelous.backend.dto.ProductRequest;
import com.marvelous.backend.dto.ProductResponse;
import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Supplier;
import com.marvelous.backend.repository.ProductRepository;
import com.marvelous.backend.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ProductService productService;

    private Supplier supplier;
    private Product product;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(10L)
                .name("Test Supplier")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .costPrice(new BigDecimal("9.99"))
                .stockQuantity(100)
                .unitOfMeasure("kg")
                .minStockAlert(5)
                .supplier(supplier)
                .build();
    }

    @Test
    void findAll_returnsAllProducts() {
        Product second = Product.builder().id(2L).name("Second").costPrice(BigDecimal.ONE)
                .stockQuantity(10).unitOfMeasure("un").minStockAlert(2).build();
        when(productRepository.findAll()).thenReturn(List.of(product, second));

        List<ProductResponse> result = productService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> result = productService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getCostPrice()).isEqualByComparingTo("9.99");
        assertThat(result.getStockQuantity()).isEqualTo(100);
        assertThat(result.getUnitOfMeasure()).isEqualTo("kg");
        assertThat(result.getSupplierId()).isEqualTo(10L);
        assertThat(result.getSupplierName()).isEqualTo("Test Supplier");
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findBySupplierId_returnsList() {
        when(productRepository.findBySupplierId(10L)).thenReturn(List.of(product));

        List<ProductResponse> result = productService.findBySupplierId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSupplierId()).isEqualTo(10L);
    }

    @Test
    void findLowStock_returnsLowStockProducts() {
        Product lowStock = Product.builder().id(5L).name("Low").costPrice(BigDecimal.ONE)
                .stockQuantity(2).unitOfMeasure("un").minStockAlert(5).build();
        when(productRepository.findLowStockProducts()).thenReturn(List.of(lowStock));

        List<ProductResponse> result = productService.findLowStock();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isLowStock()).isTrue();
    }

    @Test
    void create_withSupplier_savesProductWithSupplier() {
        ProductRequest request = buildRequest("New Product", new BigDecimal("5.00"), 50, "un", 3, 10L);
        Product saved = Product.builder().id(2L).name("New Product").costPrice(new BigDecimal("5.00"))
                .stockQuantity(50).unitOfMeasure("un").minStockAlert(3).supplier(supplier).build();

        when(supplierRepository.findById(10L)).thenReturn(Optional.of(supplier));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse result = productService.create(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getSupplierId()).isEqualTo(10L);
        verify(supplierRepository).findById(10L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_withoutSupplier_savesProductWithNullSupplier() {
        ProductRequest request = buildRequest("Solo Product", new BigDecimal("2.50"), 20, "L", null, null);
        Product saved = Product.builder().id(3L).name("Solo Product").costPrice(new BigDecimal("2.50"))
                .stockQuantity(20).unitOfMeasure("L").minStockAlert(5).build();

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse result = productService.create(request);

        assertThat(result.getSupplierId()).isNull();
        verify(supplierRepository, never()).findById(any());
    }

    @Test
    void create_withNullMinStockAlert_defaultsToFive() {
        ProductRequest request = buildRequest("Default Alert", BigDecimal.TEN, 10, "un", null, null);
        Product saved = Product.builder().id(4L).name("Default Alert").costPrice(BigDecimal.TEN)
                .stockQuantity(10).unitOfMeasure("un").minStockAlert(5).build();
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        productService.create(request);

        verify(productRepository).save(argThat(p -> p.getMinStockAlert() == 5));
    }

    @Test
    void create_supplierNotFound_throwsEntityNotFoundException() {
        ProductRequest request = buildRequest("X", BigDecimal.ONE, 1, "un", null, 99L);
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    @Test
    void update_existingProduct_withSupplier_updatesAllFields() {
        ProductRequest request = buildRequest("Updated", new BigDecimal("15.00"), 200, "m", 10, 10L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(10L)).thenReturn(Optional.of(supplier));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponse result = productService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getCostPrice()).isEqualByComparingTo("15.00");
        assertThat(result.getStockQuantity()).isEqualTo(200);
        assertThat(result.getUnitOfMeasure()).isEqualTo("m");
        assertThat(result.getMinStockAlert()).isEqualTo(10);
    }

    @Test
    void update_withNullSupplierId_setsSupplierToNull() {
        ProductRequest request = buildRequest("Updated", new BigDecimal("15.00"), 200, "m", 10, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.update(1L, request);

        verify(productRepository).save(argThat(p -> p.getSupplier() == null));
    }

    @Test
    void update_nonExistingProduct_throwsEntityNotFoundException() {
        ProductRequest request = buildRequest("X", BigDecimal.ONE, 1, "un", 5, null);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    @Test
    void update_supplierNotFound_throwsEntityNotFoundException() {
        ProductRequest request = buildRequest("X", BigDecimal.ONE, 1, "un", 5, 99L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_existingId_deletesProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsEntityNotFoundException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void findById_productWithNoSupplier_returnsNullSupplierFields() {
        Product noSupplier = Product.builder().id(7L).name("Orphan").costPrice(BigDecimal.TEN)
                .stockQuantity(5).unitOfMeasure("un").minStockAlert(2).build();
        when(productRepository.findById(7L)).thenReturn(Optional.of(noSupplier));

        ProductResponse result = productService.findById(7L);

        assertThat(result.getSupplierId()).isNull();
        assertThat(result.getSupplierName()).isNull();
    }

    @Test
    void findById_stockBelowMinAlert_marksAsLowStock() {
        Product low = Product.builder().id(8L).name("Low").costPrice(BigDecimal.ONE)
                .stockQuantity(3).unitOfMeasure("un").minStockAlert(5).build();
        when(productRepository.findById(8L)).thenReturn(Optional.of(low));

        ProductResponse result = productService.findById(8L);

        assertThat(result.isLowStock()).isTrue();
    }

    @Test
    void findById_stockAboveMinAlert_notLowStock() {
        Product ok = Product.builder().id(9L).name("OK").costPrice(BigDecimal.ONE)
                .stockQuantity(10).unitOfMeasure("un").minStockAlert(5).build();
        when(productRepository.findById(9L)).thenReturn(Optional.of(ok));

        ProductResponse result = productService.findById(9L);

        assertThat(result.isLowStock()).isFalse();
    }

    private ProductRequest buildRequest(String name, BigDecimal costPrice, Integer stock,
                                        String unit, Integer minAlert, Long supplierId) {
        ProductRequest r = new ProductRequest();
        r.setName(name);
        r.setCostPrice(costPrice);
        r.setStockQuantity(stock);
        r.setUnitOfMeasure(unit);
        r.setMinStockAlert(minAlert);
        r.setSupplierId(supplierId);
        return r;
    }
}
