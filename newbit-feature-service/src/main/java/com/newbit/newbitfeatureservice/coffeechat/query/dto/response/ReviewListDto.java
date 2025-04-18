package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "리뷰 목록용 DTO")
public class ReviewListDto {
    @Schema(description = "리뷰ID")
    private Long reviewId;
    @Schema(description = "별점")
    private BigDecimal rating;
    @Schema(description = "내용")
    private String comment;
    @Schema(description = "작성자ID")
    private Long userId;
    @Schema(description = "작성자 닉네임")
    private String nickname;
}
