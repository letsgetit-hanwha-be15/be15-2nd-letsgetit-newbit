package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "공개 시리즈 리스트 조회 응답")
public class GetSeriesListResponseDto {

    @Schema(description = "시리즈 ID", example = "1")
    private Long seriesId;

    @Schema(description = "시리즈 제목", example = "이직 준비 A to Z")
    private String title;

    @Schema(description = "시리즈 설명", example = "이직 준비 꿀팁 모음")
    private String description;

    @Schema(description = "썸네일 URL", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "멘토 ID", example = "5")
    private Long mentorId;

    @Schema(description = "멘토 닉네임", example = "개발자도토리")
    private String mentorNickname;

    @Schema(description = "작성일시", example = "2025-07-02T09:00:00")
    private LocalDateTime createdAt;

    private Integer columns;
    public void setMentorNickname(String nickname) {
        this.mentorNickname = nickname;
    }
}