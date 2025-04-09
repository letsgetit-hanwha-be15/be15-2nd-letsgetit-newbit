package com.newbit.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    @Schema(description = "게시글 제목", example = "수정된 제목")
    private String title;

    @Schema(description = "게시글 내용", example = "수정된 내용")
    private String content;
}
