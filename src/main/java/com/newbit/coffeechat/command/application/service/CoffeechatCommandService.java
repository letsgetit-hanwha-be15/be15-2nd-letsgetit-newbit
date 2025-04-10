package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.application.dto.request.RequestTimeDto;
import com.newbit.coffeechat.command.domain.aggregate.RequestTime;
import com.newbit.coffeechat.command.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.repository.RequestTimeRepository;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoffeechatCommandService {

    private final CoffeechatRepository coffeechatRepository;
    private final CoffeechatQueryService coffeechatQueryService;
    private final RequestTimeRepository requestTimeRepository;

    /* 커피챗 등록 */
    @Transactional
    public Long createCoffeechat(Long userId, CoffeechatCreateRequest request) {
        // 1. 진행중인 커피챗이 존재
        CoffeechatSearchRequest coffeechatSearchRequest = new CoffeechatSearchRequest();
        coffeechatSearchRequest.setMenteeId(userId);
        coffeechatSearchRequest.setMentorId(request.getMentorId());
        coffeechatSearchRequest.setIsProgressing(true);
        CoffeechatListResponse coffeechatDtos = coffeechatQueryService.getCoffeechats(coffeechatSearchRequest);
        if (!coffeechatDtos.getCoffeechats().isEmpty()) throw new BusinessException(ErrorCode.COFFEECHAT_ALREADY_EXIST);

        // 2. 커피챗 등록
        Coffeechat newCoffeechat = Coffeechat.of(userId, request.getMentorId(), request.getRequestMessage(), request.getPurchaseQuantity());

        Coffeechat coffeechat = coffeechatRepository.save(newCoffeechat);

        // 3. 커피챗 요청 등록(서비스 함수 생성)
        createRequestTime(coffeechat.getCoffeechatId(), request.getRequestTimes(), request.getPurchaseQuantity());

        return coffeechat.getCoffeechatId();
    }

    private void createRequestTime(Long coffeechatId, List<RequestTimeDto> requestTimeDtos, int purchaseQuantity) {
        List<RequestTime> requestTimes = new LinkedList<>();
        requestTimeDtos.forEach(timeDto -> {
            if (!timeDto.getStartDateTime().toLocalDate().isEqual(timeDto.getEndDateTime().toLocalDate())) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST_DATE); // 시작 날짜와 끝 날짜가 다릅니다.
            }

            long minutesDiff = ChronoUnit.MINUTES.between(timeDto.getStartDateTime(), timeDto.getEndDateTime());
            long requiredMinutes = purchaseQuantity * 30L;
            if (minutesDiff < requiredMinutes) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST_TIME); // 시작 시간과 끝 시간 구매 수량 x 30분 보다 작습니다.
            }
            requestTimes.add(RequestTime.of(
                    timeDto.getStartDateTime().toLocalDate(),
                    timeDto.getStartDateTime(),
                    timeDto.getEndDateTime(),
                    coffeechatId
            ));
        });

        requestTimes.forEach(requestTimeRepository::save);
    }


    @Transactional
    public void markAsPurchased(Long coffeechatId) {
        Coffeechat coffeechat = coffeechatRepository.findById(coffeechatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        if (coffeechat.getProgressStatus() != ProgressStatus.PAYMENT_WAITING) {
            throw new BusinessException(ErrorCode.COFFEECHAT_NOT_PURCHASABLE);
        }

        coffeechat.markAsPurchased();
    }
}
