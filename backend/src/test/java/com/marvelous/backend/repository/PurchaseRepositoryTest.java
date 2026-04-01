package com.marvelous.backend.repository;

import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Purchase;
import com.marvelous.backend.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PurchaseRepositoryTest {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Product product;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = supplierRepository.save(Supplier.builder().name("Vendor").build());
        product = productRepository.save(Product.builder()
                .name("Widget").costPrice(BigDecimal.TEN).stockQuantity(100).unitOfMeasure("un").build());
    }

    @Test
    void save_persistsPurchase() {
        Purchase purchase = Purchase.builder()
                .product(product).supplier(supplier)
                .quantity(5).unitPrice(new BigDecimal("9.99"))
                .purchaseDate(LocalDate.now()).build();

        Purchase saved = purchaseRepository.save(purchase);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getPurchaseDate()).isNotNull();
    }

    @Test
    void findByProductIdOrderByPurchaseDateDesc_returnsSortedByDateDesc() {
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(1).unitPrice(BigDecimal.ONE)
                .purchaseDate(LocalDate.of(2024, 1, 1)).build());
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(2).unitPrice(new BigDecimal("2.00"))
                .purchaseDate(LocalDate.of(2024, 3, 1)).build());
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(3).unitPrice(new BigDecimal("3.00"))
                .purchaseDate(LocalDate.of(2024, 2, 1)).build());

        List<Purchase> result = purchaseRepository.findByProductIdOrderByPurchaseDateDesc(product.getId());

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPurchaseDate()).isEqualTo(LocalDate.of(2024, 3, 1));
        assertThat(result.get(1).getPurchaseDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(result.get(2).getPurchaseDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void findByProductIdOrderByPurchaseDateDesc_differentProduct_returnsEmpty() {
        Product other = productRepository.save(Product.builder()
                .name("Other").costPrice(BigDecimal.ONE).stockQuantity(10).unitOfMeasure("kg").build());

        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(1).unitPrice(BigDecimal.ONE).purchaseDate(LocalDate.now()).build());

        List<Purchase> result = purchaseRepository.findByProductIdOrderByPurchaseDateDesc(other.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findTopSuppliersBySpending_returnsSuppliersSortedByTotalSpending() {
        Supplier supplier2 = supplierRepository.save(Supplier.builder().name("Supplier2").build());

        // supplier: 5 * 10 + 2 * 10 = 70
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(5).unitPrice(new BigDecimal("10.00")).purchaseDate(LocalDate.now()).build());
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(2).unitPrice(new BigDecimal("10.00")).purchaseDate(LocalDate.now()).build());

        // supplier2: 1 * 200 = 200
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier2)
                .quantity(1).unitPrice(new BigDecimal("200.00")).purchaseDate(LocalDate.now()).build());

        List<Object[]> result = purchaseRepository.findTopSuppliersBySpending();

        assertThat(result).hasSize(2);
        // supplier2 should be first (higher spending)
        assertThat(result.get(0)[1]).isEqualTo("Supplier2");
        assertThat(result.get(1)[1]).isEqualTo("Vendor");
    }

    @Test
    void findLastTwoPurchasesByProduct_returnsAtMostTwoMostRecent() {
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(1).unitPrice(new BigDecimal("5.00"))
                .purchaseDate(LocalDate.of(2024, 1, 1)).build());
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(2).unitPrice(new BigDecimal("6.00"))
                .purchaseDate(LocalDate.of(2024, 2, 1)).build());
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(3).unitPrice(new BigDecimal("7.00"))
                .purchaseDate(LocalDate.of(2024, 3, 1)).build());

        List<Purchase> result = purchaseRepository.findLastTwoPurchasesByProduct(product.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUnitPrice()).isEqualByComparingTo("7.00");
        assertThat(result.get(1).getUnitPrice()).isEqualByComparingTo("6.00");
    }

    @Test
    void findLastTwoPurchasesByProduct_onePurchase_returnsOne() {
        purchaseRepository.save(Purchase.builder().product(product).supplier(supplier)
                .quantity(1).unitPrice(new BigDecimal("5.00")).purchaseDate(LocalDate.now()).build());

        List<Purchase> result = purchaseRepository.findLastTwoPurchasesByProduct(product.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    void findLastTwoPurchasesByProduct_noPurchases_returnsEmpty() {
        List<Purchase> result = purchaseRepository.findLastTwoPurchasesByProduct(product.getId());

        assertThat(result).isEmpty();
    }
}
