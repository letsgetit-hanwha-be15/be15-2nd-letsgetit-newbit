package com.newbit.newbitfeatureservice.column.dto.response;

import com.newbit.newbitfeatureservice.column.enums.RequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "관리자용 칼럼 요청 목록 응답")
public class AdminColumnRequestResponseDto {

    @Schema(description = "칼럼 요청 ID", example = "1")
    private Long columnRequestId;

    @Schema(description = "요청 타입", example = "CREATE / UPDATE / DELETE")
    private RequestType requestType;

    @Schema(description = "승인 여부", example = "false")
    private Boolean isApproved;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "가격")
    private Integer price;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;

    @Schema(description = "요청 생성 시각")
    private LocalDateTime createdAt;

    @Schema(description = "거절 사유")
    private String rejectedReason;

    @Schema(description = "멘토 닉네임")
    private String mentorNickname;

    @Schema(description = "본문 내용")
    private String content;
}