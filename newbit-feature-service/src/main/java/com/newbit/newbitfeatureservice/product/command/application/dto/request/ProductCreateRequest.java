package com.newbit.newbitfeatureservice.product.command.application.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 생성 요청")
public class ProductCreateRequest {
    @Schema(description = "상품명", example = "베이직 패키지")
    private String name;
    
    @Schema(description = "가격", example = "1000.00")
    private BigDecimal price;
    
    @Schema(description = "다이아몬드 수량", example = "100")
    private Integer diamondAmount;
    
    @Schema(description = "상품 설명", example = "기본 다이아몬드 100개 패키지")
    private String description;
} 