package com.newbit.purchase.query.repository;

import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.AssetHistoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AssetHistoryMapper {
    List<AssetHistoryDto> findPointHistories(
            HistoryRequest request
    );

    long countPointHistories(HistoryRequest request);
}