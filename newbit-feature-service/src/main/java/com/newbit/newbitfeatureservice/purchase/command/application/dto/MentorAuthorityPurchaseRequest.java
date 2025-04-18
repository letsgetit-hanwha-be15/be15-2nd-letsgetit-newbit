// MentorAuthorityPurchaseRequest.java
package com.newbit.newbitfeatureservice.purchase.command.application.dto;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.PurchaseAssetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MentorAuthorityPurchaseRequest {
    @NotNull(message = "재화 타입은 필수입니다.")
    @Schema(description = "사용할 재화 타입 (DIAMOND or POINT)", example = "DIAMOND")
    private PurchaseAssetType assetType;
}