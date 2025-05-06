package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "공개 칼럼 상세 조회 응답")
public class GetColumnDetailResponseDto {

    @Schema(description = "칼럼 ID", example = "1")
    private Long columnId;

    @Schema(description = "칼럼 제목", example = "나의 성장 이야기")
    private String title;

    @Schema(description = "칼럼 내용", example = "이렇게 성장해왔습니다...")
    private String content;

    @Schema(description = "가격", example = "1000")
    private Integer price;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.png")
    private String thumbnailUrl;

    @Schema(description = "좋아요 수", example = "23")
    private int likeCount;

    @Schema(description = "멘토 ID", example = "3")
    private Long mentorId;

    @Schema(description = "멘토 닉네임", example = "개발자도토리")
    private String mentorNickname;

    @Schema(description = "작성일")
    private LocalDateTime createdAt;

    public GetColumnDetailResponseDto(Long columnId, String title, String content, Integer price,
                                      String thumbnailUrl, int likeCount, Long mentorId, LocalDateTime createdAt) {
        this.columnId = columnId;
        this.title = title;
        this.content = content;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.likeCount = likeCount;
        this.mentorId = mentorId;
        this.createdAt = createdAt;
    }

    public void setMentorNickname(String mentorNickname) {
        this.mentorNickname = mentorNickname;
    }
}
