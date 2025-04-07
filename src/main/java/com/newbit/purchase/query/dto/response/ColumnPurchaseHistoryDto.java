package com.newbit.purchase.query.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnPurchaseHistoryDto {
    private Long columnPurchaseId;
    private Long columnId;
    private String columnTitle;
    private String thumbnailUrl;
    private int price;
    private LocalDateTime purchasedAt;
}