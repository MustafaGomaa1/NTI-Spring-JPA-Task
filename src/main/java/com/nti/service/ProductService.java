package com.nti.service;

import com.nti.exception.ProductException;
import com.nti.model.Product;
import com.nti.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public void restock(int productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(()->new ProductException("Product not found"));
        product.setStock(product.getStock()+quantity);
        productRepository.save(product);
    }

    public void changePrice(int productId,double newPrice){
        Product product = productRepository.findById(productId).orElseThrow(()->new ProductException("Product not found"));
        product.setPrice(BigDecimal.valueOf(newPrice));
    }
}
