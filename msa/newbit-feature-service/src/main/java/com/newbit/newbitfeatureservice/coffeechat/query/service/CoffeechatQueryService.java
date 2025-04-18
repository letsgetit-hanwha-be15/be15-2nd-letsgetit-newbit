package com.newbit.newbitfeatureservice.coffeechat.query.service;

import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.*;
import com.newbit.newbitfeatureservice.coffeechat.query.mapper.CoffeechatMapper;
import com.newbit.newbitfeatureservice.common.dto.Pagination;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /* 커피챗 목록 조회 */
    @Transactional(readOnly = true)
    public CoffeechatListResponse getCoffeechats(CoffeechatSearchServiceRequest coffeechatSearchServiceRequest) {

        // 필요한 컨텐츠 조회
        List<CoffeechatDto> coffeechats = coffeechatMapper.selectCoffeechats(coffeechatSearchServiceRequest);

        // 해당 검색 조건으로 총 몇개의 컨텐츠가 있는지 조회 (페이징을 위한 속성 값 계산이 필요)
        long totalItems = coffeechatMapper.countCoffeechats(coffeechatSearchServiceRequest);

        int page = coffeechatSearchServiceRequest.getPage();
        int size = coffeechatSearchServiceRequest.getSize();


        return CoffeechatListResponse.builder()
                .coffeechats(coffeechats)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

    public RequestTimeListResponse getCoffeechatRequestTimes(Long coffeechatId) {
        // 필요한 컨텐츠 조회
        List<RequestTimeDto> requestTimes = Optional.ofNullable(coffeechatMapper.selectRequestTimeByCoffeechatId(coffeechatId))
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TIME_NOT_FOUND));

        return RequestTimeListResponse.builder()
                .requestTimes(requestTimes)
                .build();
    }
}
