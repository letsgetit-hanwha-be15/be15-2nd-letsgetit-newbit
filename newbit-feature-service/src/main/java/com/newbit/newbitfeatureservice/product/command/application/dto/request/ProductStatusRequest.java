package com.newbit.newbitfeatureservice.product.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 상태 변경 요청")
public class ProductStatusRequest {
    @Schema(description = "활성화 여부", example = "true")
    private boolean isActive;
} 