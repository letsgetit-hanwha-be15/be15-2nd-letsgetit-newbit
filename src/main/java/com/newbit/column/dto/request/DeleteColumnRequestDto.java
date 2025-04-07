package com.newbit.column.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteColumnRequestDto {
    private String reason; // 삭제 요청 사유
}
