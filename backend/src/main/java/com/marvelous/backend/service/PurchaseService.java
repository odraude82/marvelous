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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public List<PurchaseResponse> findAll() {
        return purchaseRepository.findAll()
                .stream()
                .map(PurchaseResponse::from)
                .collect(Collectors.toList());
    }

    public PurchaseResponse findById(Long id) {
        return purchaseRepository.findById(id)
                .map(PurchaseResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found with id: " + id));
    }

    public List<PurchaseResponse> findByProductId(Long productId) {
        return purchaseRepository.findByProductIdOrderByPurchaseDateDesc(productId)
                .stream()
                .map(PurchaseResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseResponse create(PurchaseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + request.getProductId()));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + request.getSupplierId()));

        Purchase purchase = Purchase.builder()
                .product(product)
                .supplier(supplier)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .purchaseDate(request.getPurchaseDate())
                .notes(request.getNotes())
                .build();

        Purchase saved = purchaseRepository.save(purchase);

        // Update product stock and cost price
        product.setStockQuantity(product.getStockQuantity() + request.getQuantity());
        product.setCostPrice(request.getUnitPrice());
        productRepository.save(product);

        return PurchaseResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new EntityNotFoundException("Purchase not found with id: " + id);
        }
        purchaseRepository.deleteById(id);
    }
}
