package com.newbit.purchase.query.repository;

import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.SaleHistoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SaleHistoryMapper {
    List<SaleHistoryDto> findSaleHistories(HistoryRequest request);
    long countSaleHistories(HistoryRequest request);
}