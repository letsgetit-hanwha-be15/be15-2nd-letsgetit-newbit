package com.newbit.newbitfeatureservice.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Schema(description = "게시글 제목", example = "수정된 제목")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "게시글 내용", example = "수정된 내용")
    private String content;
}
