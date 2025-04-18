package com.newbit.newbitfeatureservice.purchase.query.dto.request;


import com.newbit.newbitfeatureservice.purchase.query.dto.response.AssetHistoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class HistoryRequest {

    @NotNull
    @Schema(description = "사용자 ID")
    private Long userId;


    @Min(1)
    @Schema(description = "현재 페이지 번호")
    private Integer page = 1;
    @Min(1)
    @Schema(description = "페이지당 항목 수")
    private Integer size = 10;

    @Schema(description = "내역 유형", example = "INCREASE")
    private AssetHistoryType type;


    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}

