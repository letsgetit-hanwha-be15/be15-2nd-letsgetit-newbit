package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.domain.aggregate.RequestTime;
import com.newbit.coffeechat.command.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.repository.RequestTimeRepository;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoffeechatCommandService {

    private final CoffeechatRepository coffeechatRepository;
    private final CoffeechatQueryService coffeechatQueryService;
    private final RequestTimeRepository requestTimeRepository;

    /**
     * 한두 번만 사용하는 간단한 조회여서 과도한 추상화를 피하기 위해
     * requestTimeService 로직 대신 repository 직접 호출해서 사용
     */

    /* 커피챗 등록 */
    @Transactional
    public Long createCoffeechat(Long userId, CoffeechatCreateRequest request) {
        // 1. 진행중인 커피챗이 존재
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();
        coffeechatSearchServiceRequest.setMenteeId(userId);
        coffeechatSearchServiceRequest.setMentorId(request.getMentorId());
        coffeechatSearchServiceRequest.setIsProgressing(true);
        CoffeechatListResponse coffeechatDtos = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);
        if (!coffeechatDtos.getCoffeechats().isEmpty()) throw new BusinessException(ErrorCode.COFFEECHAT_ALREADY_EXIST);

        // 2. 커피챗 등록
        Coffeechat newCoffeechat = Coffeechat.of(userId, request.getMentorId(), request.getRequestMessage(), request.getPurchaseQuantity());

        Coffeechat coffeechat = coffeechatRepository.save(newCoffeechat);

        // 3. 커피챗 요청 등록(서비스 함수 생성)
        createRequestTime(coffeechat.getCoffeechatId(), request.getRequestTimes(), request.getPurchaseQuantity());

        return coffeechat.getCoffeechatId();
    }

    private void createRequestTime(Long coffeechatId, List<LocalDateTime> requestTimes, int purchaseQuantity) {
        requestTimes.forEach(time -> {
            if (time.isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.REQUEST_DATE_IN_PAST); // 시작 날짜가 오늘보다 이전입니다.
            }

            LocalDateTime endTime = time.plusMinutes(30L * purchaseQuantity);
            requestTimeRepository.save(RequestTime.of(
                    time.toLocalDate(),
                    time,
                    endTime,
                    coffeechatId
            ));
        });
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

    @Transactional
    public void acceptCoffeechatTime(Long requestTimeId) {
        // 1. requestTime 객체 찾기
        RequestTime requestTime = requestTimeRepository.findById(requestTimeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TIME_NOT_FOUND));

        // 2. 커피챗 ID로 커피챗 객체 찾기
        Coffeechat coffeechat = coffeechatRepository.findById(requestTime.getCoffeechatId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        // 3. 커피챗 객체 update하기
        coffeechat.confirmSchedule(requestTime.getStartTime());

        // 4. 해당 coffeechatId에 대한 requestTime 객체 리스트 찾기
        List<RequestTime> requests = requestTimeRepository.findAllByCoffeechatId(coffeechat.getCoffeechatId());

        // 5. 해당 객체들 삭제
        requests.forEach(req -> requestTimeRepository.deleteById(req.getRequestTimeId()));
    }
}
