package com.newbit.newbitfeatureservice.product.command.domain.repository;

import com.newbit.newbitfeatureservice.product.command.domain.aggregate.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long productId);
    void deleteById(Long productId);
    List<Product> findAll();
} 