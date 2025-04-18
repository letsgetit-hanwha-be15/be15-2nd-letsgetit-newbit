package com.newbit.newbitfeatureservice.product.command.application.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 수정 요청")
public class ProductUpdateRequest {
    @Schema(description = "상품명", example = "프리미엄 패키지")
    private String name;
    
    @Schema(description = "가격", example = "5000.00")
    private BigDecimal price;
    
    @Schema(description = "다이아몬드 수량", example = "650")
    private Integer diamondAmount;
    
    @Schema(description = "상품 설명", example = "가장 인기있는 프리미엄 다이아몬드 패키지")
    private String description;
} 