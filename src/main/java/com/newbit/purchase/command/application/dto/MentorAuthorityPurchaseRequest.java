// MentorAuthorityPurchaseRequest.java
package com.newbit.purchase.command.application.dto;

import com.newbit.purchase.command.domain.aggregate.PurchaseAssetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MentorAuthorityPurchaseRequest {

    @NotNull(message = "userId는 필수입니다.")
    @Schema(description = "구매자 유저 ID", example = "1")
    private Long userId;

    @NotNull(message = "재화 타입은 필수입니다.")
    @Schema(description = "사용할 재화 타입 (DIAMOND or POINT)", example = "DIAMOND")
    private PurchaseAssetType assetType;
}