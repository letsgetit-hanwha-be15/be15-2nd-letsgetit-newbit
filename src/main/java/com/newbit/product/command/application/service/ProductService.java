package com.newbit.product.command.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.product.command.application.dto.request.ProductCreateRequest;
import com.newbit.product.command.application.dto.request.ProductUpdateRequest;
import com.newbit.product.command.application.dto.response.ProductResponse;
import com.newbit.product.command.domain.aggregate.Product;
import com.newbit.product.command.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 모든 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 상품 ID로 상품 조회
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = findProductById(productId);
        return ProductResponse.from(product);
    }

    /**
     * 새 다이아몬드 상품 생성
     */
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        validateProductData(request.getDiamondAmount(), request.getPrice());
        validateProductNameDuplicate(request.getName());
        
        Product product = Product.createDiamondProduct(
                request.getName(),
                request.getPrice(),
                request.getDiamondAmount(),
                request.getDescription()
        );
        
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    /**
     * 상품 정보 업데이트
     */
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = findProductById(productId);
        validateProductData(request.getDiamondAmount(), request.getPrice());
        
        // 자기 자신을 제외한 동일 이름 상품 검증
        validateProductNameDuplicateExceptSelf(request.getName(), productId);
        
        product.updateProduct(
                request.getName(),
                request.getPrice(),
                request.getDiamondAmount(),
                request.getDescription()
        );
        
        Product updatedProduct = productRepository.save(product);
        return ProductResponse.from(updatedProduct);
    }

    /**
     * 상품 활성화/비활성화
     */
    @Transactional
    public ProductResponse toggleProductActivation(Long productId, boolean isActive) {
        Product product = findProductById(productId);
        
        if (isActive) {
            product.activate();
        } else {
            product.deactivate();
        }
        
        Product updatedProduct = productRepository.save(product);
        return ProductResponse.from(updatedProduct);
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void deleteProduct(Long productId) {
        findProductById(productId);
        productRepository.deleteById(productId);
    }
    
    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
    
    private void validateProductData(Integer diamondAmount, BigDecimal price) {
        if (diamondAmount <= 0) {
            throw new BusinessException(ErrorCode.PRODUCT_INVALID_DIAMOND_AMOUNT);
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_MISMATCH);
        }
    }
    
    private void validateProductNameDuplicate(String name) {
        boolean exists = productRepository.findAll().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name));
        
        if (exists) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_DUPLICATE);
        }
    }
    
    private void validateProductNameDuplicateExceptSelf(String name, Long productId) {
        boolean exists = productRepository.findAll().stream()
                .filter(p -> !p.getProductId().equals(productId))
                .anyMatch(p -> p.getName().equalsIgnoreCase(name));
        
        if (exists) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_DUPLICATE);
        }
    }
} 