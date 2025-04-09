package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.application.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeechatCommandService {

    private final CoffeechatRepository coffeechatRepository;

    /* 커피챗 등록 */
    @Transactional
    public Long createCoffeechat(Long userId, CoffeechatCreateRequest request) {
        // 1. 커피챗 등록
        Coffeechat newCoffeechat = Coffeechat.of(userId, request.getMentorId(), request.getRequestMessage(), request.getPurchaseQuantity());

        Coffeechat coffeechat = coffeechatRepository.save(newCoffeechat);

        // TODO : 2. 커피챗 요청 등록(서비스 함수 생성)


        return coffeechat.getCoffeechatId();
    }
}
