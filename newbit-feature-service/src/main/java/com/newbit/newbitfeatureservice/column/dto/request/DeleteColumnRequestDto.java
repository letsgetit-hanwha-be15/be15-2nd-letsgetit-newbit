package com.newbit.newbitfeatureservice.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "멘토 칼럼 삭제 요청 DTO (별도 요청 사유 없음)")
public class DeleteColumnRequestDto {

}
