package com.newbit.coffeechat.query.mapper;

import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoffeechatMapper {
    
    /* 커피챗 코드로 커피챗 하나 조회 */
    CoffeechatDto selectCoffeechatById(Long coffeechatId);
}
