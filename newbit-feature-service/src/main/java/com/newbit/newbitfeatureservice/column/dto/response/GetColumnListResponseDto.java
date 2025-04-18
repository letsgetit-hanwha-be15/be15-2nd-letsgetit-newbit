package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "공개 칼럼 리스트 조회 응답")
public class GetColumnListResponseDto {

    @Schema(description = "칼럼 ID", example = "1")
    private Long columnId;

    @Schema(description = "제목", example = "이직을 위한 포트폴리오 전략")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "가격", example = "1000")
    private Integer price;

    @Schema(description = "좋아요 수", example = "12")
    private Integer likeCount;

    @Schema(description = "멘토 ID", example = "5")
    private Long mentorId;

    @Schema(description = "멘토 닉네임", example = "개발자도토리")
    private String mentorNickname;

    public GetColumnListResponseDto(Long columnId, String title, String thumbnailUrl,
                                    Integer price, Integer likeCount,
                                    Long mentorId) {
        this.columnId = columnId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
        this.likeCount = likeCount;
        this.mentorId = mentorId;
    }
    public void setMentorNickname(String nickname) {
        this.mentorNickname = nickname;
    }

}

