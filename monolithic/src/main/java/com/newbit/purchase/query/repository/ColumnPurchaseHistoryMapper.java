package com.newbit.purchase.query.repository;

import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.ColumnPurchaseHistoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ColumnPurchaseHistoryMapper {
    List<ColumnPurchaseHistoryDto> findColumnPurchases(HistoryRequest request);
    long countColumnPurchases(HistoryRequest request);
}