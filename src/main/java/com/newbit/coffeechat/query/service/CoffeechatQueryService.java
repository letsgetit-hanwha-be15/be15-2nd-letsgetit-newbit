package com.newbit.coffeechat.query.service;

import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.mapper.CoffeechatMapper;
import com.newbit.common.dto.Pagination;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
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
    public CoffeechatListResponse getCoffeechats(CoffeechatSearchRequest coffeechatSearchRequest) {

        // 필요한 컨텐츠 조회
        List<CoffeechatDto> coffeechats = coffeechatMapper.selectCoffeechats(coffeechatSearchRequest);

        // 해당 검색 조건으로 총 몇개의 컨텐츠가 있는지 조회 (페이징을 위한 속성 값 계산이 필요)
        long totalItems = coffeechatMapper.countCoffeechats(coffeechatSearchRequest);

        int page = coffeechatSearchRequest.getPage();
        int size = coffeechatSearchRequest.getSize();


        return CoffeechatListResponse.builder()
                .coffeechats(coffeechats)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

}
