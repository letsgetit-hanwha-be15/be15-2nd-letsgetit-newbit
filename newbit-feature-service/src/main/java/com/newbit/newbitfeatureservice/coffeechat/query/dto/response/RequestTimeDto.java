package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "커피챗 요청 시간 DTO")
public class RequestTimeDto {
    @Schema(description = "커피챗 요청 시간 ID")
    private Long requestTimeId;
    @Schema(description = "진행 날짜")
    private LocalDate eventDate;
    @Schema(description = "시작 시간")
    private LocalDateTime startTime;
    @Schema(description = "끝 시간")
    private LocalDateTime endTime;
    @Schema(description = "커피챗 ID")
    private Long coffeechatId;
}
