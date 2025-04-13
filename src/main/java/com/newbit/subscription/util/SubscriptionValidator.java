package com.newbit.subscription.util;

import org.springframework.stereotype.Component;

import com.newbit.column.domain.Series;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SubscriptionValidator {

    public Series validateSeriesExists(Long seriesId, SeriesRepository seriesRepository) {
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> {
                    log.error("시리즈를 찾을 수 없음: seriesId={}", seriesId);
                    return new BusinessException(ErrorCode.SERIES_NOT_FOUND);
                });
    }

    public BusinessException handleSubscriptionError(Exception e, String message, Long seriesId, Long userId) {
        if (e instanceof BusinessException) {
            logError(message, seriesId, userId, e);
            return (BusinessException) e;
        }
        
        logError(message, seriesId, userId, e);
        return new BusinessException(ErrorCode.SUBSCRIPTION_PROCESSING_ERROR);
    }
    

    private void logError(String message, Long seriesId, Long userId, Exception e) {
        if (seriesId != null && userId != null) {
            log.error("{}: seriesId={}, userId={}, error={}", message, seriesId, userId, e.getMessage());
        } else if (seriesId != null) {
            log.error("{}: seriesId={}, error={}", message, seriesId, e.getMessage());
        } else if (userId != null) {
            log.error("{}: userId={}, error={}", message, userId, e.getMessage());
        } else {
            log.error("{}: error={}", message, e.getMessage());
        }
    }
} 