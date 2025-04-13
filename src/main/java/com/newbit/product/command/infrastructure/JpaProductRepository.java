package com.newbit.product.command.infrastructure;

import com.newbit.product.command.domain.aggregate.Product;
import com.newbit.product.command.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends ProductRepository, JpaRepository<Product, Long> {
} 