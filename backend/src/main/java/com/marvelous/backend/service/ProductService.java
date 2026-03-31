package com.marvelous.backend.service;

import com.marvelous.backend.dto.ProductRequest;
import com.marvelous.backend.dto.ProductResponse;
import com.marvelous.backend.model.Product;
import com.marvelous.backend.model.Supplier;
import com.marvelous.backend.repository.ProductRepository;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    public List<ProductResponse> findBySupplierId(Long supplierId) {
        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findLowStock() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + request.getSupplierId()));
        }
        Product product = Product.builder()
                .name(request.getName())
                .costPrice(request.getCostPrice())
                .stockQuantity(request.getStockQuantity())
                .unitOfMeasure(request.getUnitOfMeasure())
                .minStockAlert(request.getMinStockAlert() != null ? request.getMinStockAlert() : 5)
                .supplier(supplier)
                .build();
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + request.getSupplierId()));
            product.setSupplier(supplier);
        } else {
            product.setSupplier(null);
        }
        product.setName(request.getName());
        product.setCostPrice(request.getCostPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        product.setMinStockAlert(request.getMinStockAlert() != null ? request.getMinStockAlert() : 5);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
