package com.newbit.newbitfeatureservice.purchase.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnPurchaseHistoryDto {
    @Schema(description = "칼럼 ID", example = "55")
    private Long columnId;

    @Schema(description = "칼럼 제목", example = "백엔드 개발자의 성장기")
    private String columnTitle;

    @Schema(description = "칼럼 썸네일 이미지 URL", example = "https://newbit.s3.amazonaws.com/thumbnail1.jpg")
    private String thumbnailUrl;

    @Schema(description = "구매 가격", example = "50")
    private int price;

    @Schema(description = "구매 일시", example = "2025-04-08T13:00:00")
    private LocalDateTime purchasedAt;
}