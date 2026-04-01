package com.marvelous.backend.repository;

import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = supplierRepository.save(Supplier.builder()
                .name("Test Supplier")
                .cnpjCpf("12.345.678/0001-99")
                .build());
    }

    @Test
    void save_persistsProduct() {
        Product product = Product.builder()
                .name("Widget")
                .costPrice(new BigDecimal("9.99"))
                .stockQuantity(100)
                .unitOfMeasure("un")
                .minStockAlert(5)
                .supplier(supplier)
                .build();

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Widget");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_existingProduct_returnsProduct() {
        Product product = productRepository.save(Product.builder()
                .name("Gadget").costPrice(BigDecimal.TEN).stockQuantity(20).unitOfMeasure("kg").build());

        Optional<Product> result = productRepository.findById(product.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Gadget");
    }

    @Test
    void findBySupplierId_returnsProductsForSupplier() {
        productRepository.save(Product.builder()
                .name("P1").costPrice(BigDecimal.ONE).stockQuantity(10).unitOfMeasure("un").supplier(supplier).build());
        productRepository.save(Product.builder()
                .name("P2").costPrice(BigDecimal.TEN).stockQuantity(5).unitOfMeasure("kg").supplier(supplier).build());

        Supplier otherSupplier = supplierRepository.save(Supplier.builder().name("Other").build());
        productRepository.save(Product.builder()
                .name("P3").costPrice(BigDecimal.ONE).stockQuantity(1).unitOfMeasure("L").supplier(otherSupplier).build());

        List<Product> result = productRepository.findBySupplierId(supplier.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName).containsExactlyInAnyOrder("P1", "P2");
    }

    @Test
    void findBySupplierId_noProducts_returnsEmpty() {
        Supplier emptySupplier = supplierRepository.save(Supplier.builder().name("Empty").build());

        List<Product> result = productRepository.findBySupplierId(emptySupplier.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findLowStockProducts_returnsProductsAtOrBelowMinAlert() {
        // Low stock: stockQuantity <= minStockAlert
        productRepository.save(Product.builder()
                .name("Low1").costPrice(BigDecimal.ONE).stockQuantity(3).unitOfMeasure("un").minStockAlert(5).build());
        productRepository.save(Product.builder()
                .name("ExactMin").costPrice(BigDecimal.ONE).stockQuantity(5).unitOfMeasure("un").minStockAlert(5).build());
        productRepository.save(Product.builder()
                .name("Sufficient").costPrice(BigDecimal.ONE).stockQuantity(10).unitOfMeasure("un").minStockAlert(5).build());

        List<Product> result = productRepository.findLowStockProducts();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName).containsExactlyInAnyOrder("Low1", "ExactMin");
    }

    @Test
    void findLowStockProducts_noLowStock_returnsEmpty() {
        productRepository.save(Product.builder()
                .name("Ok").costPrice(BigDecimal.ONE).stockQuantity(50).unitOfMeasure("un").minStockAlert(5).build());

        List<Product> result = productRepository.findLowStockProducts();

        assertThat(result).isEmpty();
    }

    @Test
    void delete_removesProduct() {
        Product product = productRepository.save(Product.builder()
                .name("ToDelete").costPrice(BigDecimal.ONE).stockQuantity(1).unitOfMeasure("un").build());
        Long id = product.getId();

        productRepository.deleteById(id);

        assertThat(productRepository.findById(id)).isEmpty();
    }
}
