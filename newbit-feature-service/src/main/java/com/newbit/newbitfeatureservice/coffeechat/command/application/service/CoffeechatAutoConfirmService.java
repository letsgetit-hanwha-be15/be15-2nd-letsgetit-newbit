package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.coffeechat.query.mapper.CoffeechatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeechatAutoConfirmService {
    private final CoffeechatMapper coffeechatMapper;
    private final RestTemplate restTemplate;
    @Value("${coffeechat.confirm.url:http://localhost:8080/api/v1/coffeechats/confirm-purchase}")
    private String confirmBaseUrl; // {coffeechatId}

    /**
     * 매일 자정에 실행되는 스케줄러
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoConfirmCoffeechats() {
        // 오늘 날짜에서 7일 뺀 날짜를 기준으로 조회
        LocalDate targetDate = LocalDate.now().minusDays(7);
        List<Long> coffeechatIds = coffeechatMapper.selectCoffeechatIdByEndDate(targetDate);

        coffeechatIds.forEach(coffeechatId -> {
            try {
                String url = confirmBaseUrl + "/" + coffeechatId;
                restTemplate.put(url, null); // 구매 확정 api 호출
                log.info("커피챗 [ID: {}] 구매확정 완료", coffeechatId);
            } catch (Exception e) {
                log.error("커피챗 [ID: {}] 구매확정 API 호출 중 오류 발생: {}", coffeechatId, e.getMessage());
            }
        });
    }
}
