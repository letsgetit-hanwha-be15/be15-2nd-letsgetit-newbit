package com.newbit.newbitfeatureservice.purchase.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ColumnPurchaseRequest {
    @NotNull(message = "columnId는 필수입니다.")
    @Schema(description = "컬럼 ID", example = "1")
    private Long columnId;
}