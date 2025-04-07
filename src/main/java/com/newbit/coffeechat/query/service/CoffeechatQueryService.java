package com.newbit.coffeechat.query.service;

import com.newbit.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.mapper.CoffeechatMapper;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoffeechatQueryService {

    private final CoffeechatMapper coffeechatMapper;

    /* 커피챗 상세 조회 */
    @Transactional(readOnly = true)
    public CoffeechatDetailResponse getCoffeechat(Long coffeechatId) {

        CoffeechatDto coffeechat = Optional.ofNullable(coffeechatMapper.selectCoffeechatById(coffeechatId))
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        return CoffeechatDetailResponse.builder()
                .coffeechat(coffeechat)
                .build();
    }
}
