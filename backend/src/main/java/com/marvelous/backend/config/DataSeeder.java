package com.marvelous.backend.config;

import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Purchase;
import com.marvelous.backend.model.Supplier;
import com.marvelous.backend.repository.ProductRepository;
import com.marvelous.backend.repository.PurchaseRepository;
import com.marvelous.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(SupplierRepository supplierRepo,
                               ProductRepository productRepo,
                               PurchaseRepository purchaseRepo) {
        return args -> {
            if (supplierRepo.count() > 0) return;

            log.info("Seeding sample data...");

            Supplier s1 = supplierRepo.save(Supplier.builder()
                    .name("Distribuidora ABC")
                    .cnpjCpf("12.345.678/0001-90")
                    .contact("(11) 9999-1111")
                    .productCategory("Alimentos")
                    .build());

            Supplier s2 = supplierRepo.save(Supplier.builder()
                    .name("Papelaria Central")
                    .cnpjCpf("98.765.432/0001-10")
                    .contact("(11) 8888-2222")
                    .productCategory("Escritório")
                    .build());

            Supplier s3 = supplierRepo.save(Supplier.builder()
                    .name("Limpeza Express")
                    .cnpjCpf("11.222.333/0001-44")
                    .contact("(11) 7777-3333")
                    .productCategory("Higiene e Limpeza")
                    .build());

            Product p1 = productRepo.save(Product.builder()
                    .name("Arroz Branco 5kg")
                    .costPrice(new BigDecimal("18.50"))
                    .stockQuantity(50)
                    .unitOfMeasure("Pacote")
                    .minStockAlert(10)
                    .supplier(s1)
                    .build());

            Product p2 = productRepo.save(Product.builder()
                    .name("Feijão Preto 1kg")
                    .costPrice(new BigDecimal("7.90"))
                    .stockQuantity(3)
                    .unitOfMeasure("Pacote")
                    .minStockAlert(10)
                    .supplier(s1)
                    .build());

            Product p3 = productRepo.save(Product.builder()
                    .name("Caneta Azul (cx 50)")
                    .costPrice(new BigDecimal("25.00"))
                    .stockQuantity(15)
                    .unitOfMeasure("Caixa")
                    .minStockAlert(5)
                    .supplier(s2)
                    .build());

            Product p4 = productRepo.save(Product.builder()
                    .name("Detergente 500ml")
                    .costPrice(new BigDecimal("3.20"))
                    .stockQuantity(2)
                    .unitOfMeasure("Unidade")
                    .minStockAlert(5)
                    .supplier(s3)
                    .build());

            // Purchase history for price variation
            purchaseRepo.save(Purchase.builder()
                    .product(p1)
                    .supplier(s1)
                    .quantity(20)
                    .unitPrice(new BigDecimal("17.50"))
                    .purchaseDate(LocalDate.now().minusMonths(2))
                    .notes("Primeira compra")
                    .build());

            purchaseRepo.save(Purchase.builder()
                    .product(p1)
                    .supplier(s1)
                    .quantity(30)
                    .unitPrice(new BigDecimal("18.50"))
                    .purchaseDate(LocalDate.now().minusMonths(1))
                    .notes("Segunda compra - aumento de preço")
                    .build());

            purchaseRepo.save(Purchase.builder()
                    .product(p2)
                    .supplier(s1)
                    .quantity(10)
                    .unitPrice(new BigDecimal("7.00"))
                    .purchaseDate(LocalDate.now().minusMonths(3))
                    .build());

            purchaseRepo.save(Purchase.builder()
                    .product(p2)
                    .supplier(s1)
                    .quantity(5)
                    .unitPrice(new BigDecimal("7.90"))
                    .purchaseDate(LocalDate.now().minusWeeks(2))
                    .build());

            purchaseRepo.save(Purchase.builder()
                    .product(p3)
                    .supplier(s2)
                    .quantity(5)
                    .unitPrice(new BigDecimal("25.00"))
                    .purchaseDate(LocalDate.now().minusMonths(1))
                    .build());

            purchaseRepo.save(Purchase.builder()
                    .product(p4)
                    .supplier(s3)
                    .quantity(10)
                    .unitPrice(new BigDecimal("3.20"))
                    .purchaseDate(LocalDate.now().minusWeeks(3))
                    .build());

            log.info("Sample data seeded successfully.");
        };
    }
}
