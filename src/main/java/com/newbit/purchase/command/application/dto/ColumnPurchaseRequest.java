package com.newbit.purchase.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnPurchaseRequest {

    @NotNull(message = "userId는 필수입니다.")
    @Schema(description = "구매자 유저 ID", example = "1")
    private Long userId;

    @NotNull(message = "columnId는 필수입니다.")
    @Schema(description = "컬럼 ID", example = "1")
    private Long columnId;
}