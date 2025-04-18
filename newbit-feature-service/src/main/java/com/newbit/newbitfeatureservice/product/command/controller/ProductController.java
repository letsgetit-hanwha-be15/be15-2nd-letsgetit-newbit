package com.newbit.newbitfeatureservice.product.command.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.product.command.application.dto.request.ProductCreateRequest;
import com.newbit.newbitfeatureservice.product.command.application.dto.request.ProductStatusRequest;
import com.newbit.newbitfeatureservice.product.command.application.dto.request.ProductUpdateRequest;
import com.newbit.newbitfeatureservice.product.command.application.dto.response.ProductResponse;
import com.newbit.newbitfeatureservice.product.command.application.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "상품 관리 API", description = "상품 관리 API(조회,등록,수정,활성화,비활성화)")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "전체 상품 목록 조회", description = "모든 상품 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "상품 단일 조회", description = "ID로 특정 상품 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "상품 ID") @PathVariable("id") Long productId) {
        ProductResponse product = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다. 관리자만 접근 가능합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody ProductCreateRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(product));
    }

    @Operation(summary = "상품 정보 수정", description = "기존 상품 정보를 수정합니다. 관리자만 접근 가능합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "상품 ID") @PathVariable("id") Long productId,
            @RequestBody ProductUpdateRequest request) {
        
        ProductResponse updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedProduct));
    }

    @Operation(summary = "상품 활성화/비활성화", description = "상품의 활성화 상태를 변경합니다. 관리자만 접근 가능합니다.")
    @PutMapping("/{id}/activation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> toggleProductActivation(
            @Parameter(description = "상품 ID") @PathVariable("id") Long productId,
            @RequestBody ProductStatusRequest request) {
        
        ProductResponse product = productService.toggleProductActivation(productId, request.isActive());
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다. 관리자만 접근 가능합니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "상품 ID") @PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 