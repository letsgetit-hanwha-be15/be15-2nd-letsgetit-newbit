package com.newbit.coffeechat.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "리뷰 DTO")
public class ReviewListDto {
    private Long reviewId;
    private BigDecimal rating;
    private String comment;
}
