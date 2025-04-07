package com.newbit.purchase.query.dto.request;


import com.newbit.purchase.query.dto.response.AssetHistoryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryRequest {

    //TODO: 스프링 시큐리티 + JWT 기반 인증 구조로 변경시 제거
    @NotNull
    private Long userId;


    @Min(1)
    private Integer page = 1;
    @Min(1)
    private Integer size = 10;

    private AssetHistoryType type;


    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}

