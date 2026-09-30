package com.nti.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.nti.exception.ProductException;
import com.nti.model.Product;
import com.nti.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void restock(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found"));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    public void changePrice(int productId, BigDecimal newPrice) {
        if (newPrice == null || newPrice.signum() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found"));
        product.setPrice(newPrice);
        productRepository.save(product);
    }
}
