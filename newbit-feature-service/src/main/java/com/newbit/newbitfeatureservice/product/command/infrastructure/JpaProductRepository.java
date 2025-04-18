package com.newbit.newbitfeatureservice.product.command.infrastructure;

import com.newbit.newbitfeatureservice.product.command.domain.aggregate.Product;
import com.newbit.newbitfeatureservice.product.command.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends ProductRepository, JpaRepository<Product, Long> {
} 