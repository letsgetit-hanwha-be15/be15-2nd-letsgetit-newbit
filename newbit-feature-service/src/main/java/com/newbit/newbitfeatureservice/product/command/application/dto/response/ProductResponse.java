package com.newbit.newbitfeatureservice.product.command.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.product.command.domain.aggregate.Product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 응답")
public class ProductResponse {
    @Schema(description = "상품 ID", example = "1")
    private Long productId;
    
    @Schema(description = "상품명", example = "베이직 패키지")
    private String name;
    
    @Schema(description = "가격", example = "1000.00")
    private BigDecimal price;
    
    @Schema(description = "다이아몬드 수량", example = "100")
    private Integer diamondAmount;
    
    @Schema(description = "상품 설명", example = "기본 다이아몬드 100개 패키지")
    private String description;
    
    @Schema(description = "활성화 여부", example = "true")
    private boolean isActive;
    
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .diamondAmount(product.getDiamondAmount())
                .description(product.getDescription())
                .isActive(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
} 