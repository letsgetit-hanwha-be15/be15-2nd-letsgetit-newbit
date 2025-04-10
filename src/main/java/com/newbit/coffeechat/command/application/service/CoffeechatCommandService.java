package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.application.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CoffeechatCommandService {

    private final CoffeechatRepository coffeechatRepository;
    private final CoffeechatQueryService coffeechatQueryService;

    /* 커피챗 등록 */
    @Transactional
    public Long createCoffeechat(Long userId, CoffeechatCreateRequest request) {
        // 1. 진행중인 커피챗이 존재
        CoffeechatSearchRequest coffeechatSearchRequest = new CoffeechatSearchRequest();
        coffeechatSearchRequest.setMenteeId(userId);
        coffeechatSearchRequest.setMentorId(request.getMentorId());
        coffeechatSearchRequest.setIsProgressing(true);
        CoffeechatListResponse coffeechatDtos = coffeechatQueryService.getCoffeechats(coffeechatSearchRequest);
        if(!coffeechatDtos.getCoffeechats().isEmpty()) throw new BusinessException(ErrorCode.COFFEECHAT_ALREADY_EXIST);

        // 2. 커피챗 등록
        Coffeechat newCoffeechat = Coffeechat.of(userId, request.getMentorId(), request.getRequestMessage(), request.getPurchaseQuantity());

        Coffeechat coffeechat = coffeechatRepository.save(newCoffeechat);

        // TODO : 2. 커피챗 요청 등록(서비스 함수 생성)


        return coffeechat.getCoffeechatId();
    }
}
