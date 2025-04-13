package com.newbit.subscription.integration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.subscription.dto.response.SubscriptionResponse;
import com.newbit.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.subscription.service.SubscriptionService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("구독 통합 테스트")
class SubscriptionIntegrationTest {

    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Long testUserId;
    private Long testSeriesId;
    
    @BeforeEach
    void setUp() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + uniqueId + "@example.com";
        String uniqueSeriesTitle = "통합테스트 시리즈 " + uniqueId;
        
        jdbcTemplate.update(
            "INSERT INTO user (email, nickname, password, phone_number, user_name, point, diamond, authority, is_suspended, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
            uniqueEmail, "테스트유저", "password", "01012345678", "테스트유저", 
            0, 0, "USER", false
        );
        
        testUserId = jdbcTemplate.queryForObject(
            "SELECT user_id FROM user WHERE email = ?", 
            Long.class, 
            uniqueEmail
        );
        
        jdbcTemplate.update(
            "INSERT INTO mentor (user_id, temperature, preferred_time, is_active, price, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, NOW(), NOW())",
            testUserId, 75.0, "09:00-12:00", true, 1000
        );
        
        Long mentorId = jdbcTemplate.queryForObject(
            "SELECT mentor_id FROM mentor WHERE user_id = ?", 
            Long.class, 
            testUserId
        );
        
        jdbcTemplate.update(
            "INSERT INTO series (title, description, thumbnail_url, created_at, updated_at, mentor_id) " +
            "VALUES (?, ?, ?, NOW(), NOW(), ?)",
            uniqueSeriesTitle, "시리즈 설명입니다.", "thumbnail.jpg", mentorId
        );
        
        testSeriesId = jdbcTemplate.queryForObject(
            "SELECT series_id FROM series WHERE title = ?", 
            Long.class, 
            uniqueSeriesTitle
        );
    }
    
    @AfterEach
    void cleanup() {
        jdbcTemplate.update("DELETE FROM subscription_list WHERE user_id = ? OR series_id = ?", testUserId, testSeriesId);
        jdbcTemplate.update("DELETE FROM series WHERE mentor_id IN (SELECT mentor_id FROM mentor WHERE user_id = ?)", testUserId);
        jdbcTemplate.update("DELETE FROM mentor WHERE user_id = ?", testUserId);
        jdbcTemplate.update("DELETE FROM user WHERE user_id = ?", testUserId);
    }
    
    @Test
    @DisplayName("시리즈 구독 토글 통합 테스트")
    void toggleSubscriptionTest() {
        // Given
        SubscriptionStatusResponse initialStatus = subscriptionService.getSubscriptionStatus(testSeriesId, testUserId);
        assertThat(initialStatus.isSubscribed()).isFalse();
        assertThat(initialStatus.getTotalSubscribers()).isEqualTo(0);
        
        // When - 구독 추가
        SubscriptionResponse addResponse = subscriptionService.toggleSubscription(testSeriesId, testUserId);
        
        // Then - 응답 확인
        assertThat(addResponse).isNotNull();
        assertThat(addResponse.getUserId()).isEqualTo(testUserId);
        assertThat(addResponse.getSeriesId()).isEqualTo(testSeriesId);
        assertThat(addResponse.isSubscribed()).isTrue();
        
        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM subscription_list WHERE user_id = ? AND series_id = ?",
            Integer.class,
            testUserId,
            testSeriesId
        );
        assertThat(count).isEqualTo(1);
        
        SubscriptionStatusResponse afterAddStatus = subscriptionService.getSubscriptionStatus(testSeriesId, testUserId);
        assertThat(afterAddStatus.isSubscribed()).isTrue();
        assertThat(afterAddStatus.getTotalSubscribers()).isEqualTo(1);
        
        // When - 구독 취소
        SubscriptionResponse cancelResponse = subscriptionService.toggleSubscription(testSeriesId, testUserId);
        
        // Then - 응답 확인
        assertThat(cancelResponse).isNotNull();
        assertThat(cancelResponse.getUserId()).isEqualTo(testUserId);
        assertThat(cancelResponse.getSeriesId()).isEqualTo(testSeriesId);
        assertThat(cancelResponse.isSubscribed()).isFalse();
        
        count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM subscription_list WHERE user_id = ? AND series_id = ?",
            Integer.class,
            testUserId,
            testSeriesId
        );
        assertThat(count).isEqualTo(0);
        
        SubscriptionStatusResponse afterCancelStatus = subscriptionService.getSubscriptionStatus(testSeriesId, testUserId);
        assertThat(afterCancelStatus.isSubscribed()).isFalse();
        assertThat(afterCancelStatus.getTotalSubscribers()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("사용자 구독 목록 조회 통합 테스트")
    void getUserSubscriptionsTest() {
        // Given
        Long secondSeriesId = createAdditionalSeries("테스트 시리즈 2");
        List<SubscriptionResponse> initialSubscriptions = subscriptionService.getUserSubscriptions(testUserId);
        assertThat(initialSubscriptions).isEmpty();
        
        // When
        subscriptionService.toggleSubscription(testSeriesId, testUserId);
        subscriptionService.toggleSubscription(secondSeriesId, testUserId);
        List<SubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptions(testUserId);
        
        // Then
        assertThat(subscriptions).hasSize(2);
        assertThat(subscriptions).extracting("seriesId")
                                 .containsExactlyInAnyOrder(testSeriesId, secondSeriesId);
        assertThat(subscriptions).extracting("subscribed")
                                 .containsOnly(true);
    }
    
    @Test
    @DisplayName("시리즈 구독자 목록 조회 통합 테스트")
    void getSeriesSubscribersTest() {
        // Given
        Long secondUserId = createAdditionalUser("test2@example.com");
        List<Long> initialSubscribers = subscriptionService.getSeriesSubscribers(testSeriesId);
        assertThat(initialSubscribers).isEmpty();
        
        // When
        subscriptionService.toggleSubscription(testSeriesId, testUserId);
        subscriptionService.toggleSubscription(testSeriesId, secondUserId);
        List<Long> subscribers = subscriptionService.getSeriesSubscribers(testSeriesId);
        
        // Then
        assertThat(subscribers).hasSize(2);
        assertThat(subscribers).containsExactlyInAnyOrder(testUserId, secondUserId);
    }
    
    @Test
    @DisplayName("존재하지 않는 시리즈 구독 시도 시 예외 발생 테스트")
    void nonExistingSeriesSubscriptionTest() {
        // Given
        Long nonExistingSeriesId = 9999L;
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            subscriptionService.toggleSubscription(nonExistingSeriesId, testUserId)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SERIES_NOT_FOUND);
    }
    
    private Long createAdditionalSeries(String title) {
        String uniqueTitle = title + "_" + System.currentTimeMillis();
        jdbcTemplate.update(
            "INSERT INTO series (title, description, thumbnail_url, created_at, updated_at, mentor_id) " +
            "VALUES (?, ?, ?, NOW(), NOW(), ?)",
            uniqueTitle, "시리즈 설명입니다.", "thumbnail.jpg",
            jdbcTemplate.queryForObject("SELECT mentor_id FROM mentor WHERE user_id = ?", Long.class, testUserId)
        );
        
        return jdbcTemplate.queryForObject(
            "SELECT series_id FROM series WHERE title = ?", 
            Long.class,
            uniqueTitle
        );
    }
    
    private Long createAdditionalUser(String email) {
        String uniqueEmail = email + "_" + System.currentTimeMillis();
        jdbcTemplate.update(
            "INSERT INTO user (email, nickname, password, phone_number, user_name, point, diamond, authority, is_suspended, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
            uniqueEmail, "테스트유저2", "password", "01087654321", "테스트유저2", 
            0, 0, "USER", false
        );
        
        return jdbcTemplate.queryForObject(
            "SELECT user_id FROM user WHERE email = ?", 
            Long.class, 
            uniqueEmail
        );
    }
} 