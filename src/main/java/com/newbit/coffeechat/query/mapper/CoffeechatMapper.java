package com.newbit.coffeechat.query.mapper;

import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.dto.response.RequestTimeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoffeechatMapper {
    
    /* 커피챗 코드로 커피챗 하나 조회 */
    CoffeechatDto selectCoffeechatById(Long coffeechatId);

    List<CoffeechatDto> selectCoffeechats(CoffeechatSearchServiceRequest coffeechatSearchServiceRequest);

    long countCoffeechats(CoffeechatSearchServiceRequest coffeechatSearchServiceRequest);

    List<RequestTimeDto> selectRequestTimeByCoffeechatId(Long coffeechatId);
}
