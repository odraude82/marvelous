package com.marvelous.backend.service;

import com.marvelous.backend.dto.PurchaseRequest;
import com.marvelous.backend.dto.PurchaseResponse;
import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Purchase;
import com.marvelous.backend.model.Supplier;
import com.marvelous.backend.repository.ProductRepository;
import com.marvelous.backend.repository.PurchaseRepository;
import com.marvelous.backend.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    private Supplier supplier;
    private Product product;
    private Purchase purchase;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder().id(10L).name("Best Supplier").build();

        product = Product.builder()
                .id(1L).name("Widget").costPrice(new BigDecimal("5.00"))
                .stockQuantity(50).unitOfMeasure("un").minStockAlert(5)
                .build();

        purchase = Purchase.builder()
                .id(100L).product(product).supplier(supplier)
                .quantity(10).unitPrice(new BigDecimal("4.50"))
                .purchaseDate(LocalDate.of(2024, 1, 15))
                .notes("Test notes")
                .build();
    }

    @Test
    void findAll_returnsAllPurchases() {
        Purchase second = Purchase.builder().id(200L).product(product).supplier(supplier)
                .quantity(5).unitPrice(new BigDecimal("4.00"))
                .purchaseDate(LocalDate.of(2024, 2, 1)).build();
        when(purchaseRepository.findAll()).thenReturn(List.of(purchase, second));

        List<PurchaseResponse> result = purchaseService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        assertThat(result.get(1).getId()).isEqualTo(200L);
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(purchaseRepository.findAll()).thenReturn(List.of());

        List<PurchaseResponse> result = purchaseService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsPurchase() {
        when(purchaseRepository.findById(100L)).thenReturn(Optional.of(purchase));

        PurchaseResponse result = purchaseService.findById(100L);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Widget");
        assertThat(result.getSupplierId()).isEqualTo(10L);
        assertThat(result.getSupplierName()).isEqualTo("Best Supplier");
        assertThat(result.getQuantity()).isEqualTo(10);
        assertThat(result.getUnitPrice()).isEqualByComparingTo("4.50");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("45.00");
        assertThat(result.getNotes()).isEqualTo("Test notes");
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(purchaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findByProductId_returnsPurchasesForProduct() {
        when(purchaseRepository.findByProductIdOrderByPurchaseDateDesc(1L)).thenReturn(List.of(purchase));

        List<PurchaseResponse> result = purchaseService.findByProductId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
    }

    @Test
    void create_validRequest_savesPurchaseAndUpdatesStock() {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setSupplierId(10L);
        request.setQuantity(20);
        request.setUnitPrice(new BigDecimal("6.00"));
        request.setPurchaseDate(LocalDate.of(2024, 3, 1));
        request.setNotes("Bulk order");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(10L)).thenReturn(Optional.of(supplier));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        PurchaseResponse result = purchaseService.create(request);

        assertThat(result).isNotNull();
        // Stock should be updated: 50 + 20 = 70
        verify(productRepository).save(argThat(p -> p.getStockQuantity() == 70));
        // Cost price should be updated to unit price
        verify(productRepository).save(argThat(p -> p.getCostPrice().compareTo(new BigDecimal("6.00")) == 0));
    }

    @Test
    void create_productNotFound_throwsEntityNotFoundException() {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(999L);
        request.setSupplierId(10L);
        request.setQuantity(1);
        request.setUnitPrice(BigDecimal.ONE);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void create_supplierNotFound_throwsEntityNotFoundException() {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setSupplierId(999L);
        request.setQuantity(1);
        request.setUnitPrice(BigDecimal.ONE);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void delete_existingId_deletesPurchase() {
        when(purchaseRepository.existsById(100L)).thenReturn(true);

        purchaseService.delete(100L);

        verify(purchaseRepository).deleteById(100L);
    }

    @Test
    void delete_nonExistingId_throwsEntityNotFoundException() {
        when(purchaseRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> purchaseService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(purchaseRepository, never()).deleteById(any());
    }

    @Test
    void create_totalAmount_isQuantityTimesUnitPrice() {
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setSupplierId(10L);
        request.setQuantity(3);
        request.setUnitPrice(new BigDecimal("7.00"));

        Purchase savedPurchase = Purchase.builder()
                .id(300L).product(product).supplier(supplier)
                .quantity(3).unitPrice(new BigDecimal("7.00"))
                .purchaseDate(LocalDate.now()).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(supplierRepository.findById(10L)).thenReturn(Optional.of(supplier));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        PurchaseResponse result = purchaseService.create(request);

        assertThat(result.getTotalAmount()).isEqualByComparingTo("21.00");
    }
}
