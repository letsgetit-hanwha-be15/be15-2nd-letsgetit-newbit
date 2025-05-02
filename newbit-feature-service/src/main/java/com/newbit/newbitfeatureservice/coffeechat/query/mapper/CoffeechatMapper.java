package com.newbit.newbitfeatureservice.coffeechat.query.mapper;

import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.CoffeechatProgressServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatListDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.RequestTimeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CoffeechatMapper {
    
    /* 커피챗 코드로 커피챗 하나 조회 */
    CoffeechatDto selectCoffeechatById(Long coffeechatId);

    List<CoffeechatListDto> selectCoffeechats(CoffeechatSearchServiceRequest coffeechatSearchServiceRequest);

    long countCoffeechats(CoffeechatSearchServiceRequest coffeechatSearchServiceRequest);

    long countProgressCoffeechats(CoffeechatProgressServiceRequest coffeechatProgressServiceRequest);

    List<RequestTimeDto> selectRequestTimeByCoffeechatId(Long coffeechatId);

    List<Long> selectCoffeechatIdByEndDate(@Param("targetDate") LocalDate targetDate);
}
