package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "커피챗 상세 DTO")
public class CoffeechatDto {
    @Schema(description = "커피챗ID")
    private Long coffeechatId;
    @Schema(description = "진행상태")
    private ProgressStatus progressStatus;
    @Schema(description = "요청메시지")
    private String requestMessage;
    @Schema(description = "구매 수량")
    private Integer purchaseQuantity;
    @Schema(description = "확정일시")
    private LocalDateTime confirmedSchedule;
    @Schema(description = "끝일시")
    private LocalDateTime endedAt;
    @Schema(description = "취소 일시")
    private LocalDateTime updatedAt;
    @Schema(description = "취소 사유")
    private String reason;
    @Schema(description = "판매확정일시")
    private LocalDateTime saleConfirmedAt;
    @Schema(description = "구매확정일시")
    private LocalDateTime purchaseConfirmedAt;
    @Schema(description = "멘토 ID")
    private Long mentorId;
    @Schema(description = "멘티 ID")
    private Long menteeId;
}