package com.newbit.subscription.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.subscription.dto.response.SubscriptionResponse;
import com.newbit.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.subscription.service.SubscriptionService;
import com.newbit.common.exception.ErrorCode;

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
        
        // 테스트 사용자 생성
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
        
        // 테스트 멘토 생성
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
        
        // 테스트 시리즈 생성
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
    
    @Test
    @DisplayName("시리즈 구독 토글 통합 테스트")
    void toggleSubscriptionTest() {
        SubscriptionStatusResponse initialStatus = subscriptionService.getSubscriptionStatus(testSeriesId, testUserId);
        assertThat(initialStatus.isSubscribed()).isFalse();
        assertThat(initialStatus.getTotalSubscribers()).isEqualTo(0);
        
        SubscriptionResponse addResponse = subscriptionService.toggleSubscription(testSeriesId, testUserId);
        
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
        
        SubscriptionResponse cancelResponse = subscriptionService.toggleSubscription(testSeriesId, testUserId);
        
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
        // 시리즈 2개 추가 생성
        Long secondSeriesId = createAdditionalSeries("테스트 시리즈 2");
        
        // 초기 상태 확인
        List<SubscriptionResponse> initialSubscriptions = subscriptionService.getUserSubscriptions(testUserId);
        assertThat(initialSubscriptions).isEmpty();
        
        // 구독 2개 추가
        subscriptionService.toggleSubscription(testSeriesId, testUserId);
        subscriptionService.toggleSubscription(secondSeriesId, testUserId);
        
        // 구독 목록 확인
        List<SubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptions(testUserId);
        assertThat(subscriptions).hasSize(2);
        assertThat(subscriptions).extracting("seriesId")
                                 .containsExactlyInAnyOrder(testSeriesId, secondSeriesId);
        assertThat(subscriptions).extracting("subscribed")
                                 .containsOnly(true);
    }
    
    @Test
    @DisplayName("시리즈 구독자 목록 조회 통합 테스트")
    void getSeriesSubscribersTest() {
        // 사용자 추가 생성
        Long secondUserId = createAdditionalUser("test2@example.com");
        
        // 초기 상태 확인
        List<Long> initialSubscribers = subscriptionService.getSeriesSubscribers(testSeriesId);
        assertThat(initialSubscribers).isEmpty();
        
        // 구독 2개 추가 (서로 다른 사용자)
        subscriptionService.toggleSubscription(testSeriesId, testUserId);
        subscriptionService.toggleSubscription(testSeriesId, secondUserId);
        
        // 구독자 목록 확인
        List<Long> subscribers = subscriptionService.getSeriesSubscribers(testSeriesId);
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
        jdbcTemplate.update(
            "INSERT INTO series (title, description, thumbnail_url, created_at, updated_at, mentor_id) " +
            "VALUES (?, ?, ?, NOW(), NOW(), ?)",
            title, "시리즈 설명입니다.", "thumbnail.jpg",
            jdbcTemplate.queryForObject("SELECT mentor_id FROM mentor WHERE user_id = ?", Long.class, testUserId)
        );
        
        return jdbcTemplate.queryForObject(
            "SELECT series_id FROM series WHERE title = ?", 
            Long.class,
            title
        );
    }
    
    private Long createAdditionalUser(String email) {
        jdbcTemplate.update(
            "INSERT INTO user (email, nickname, password, phone_number, user_name, point, diamond, authority, is_suspended, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
            email, "테스트유저2", "password", "01087654321", "테스트유저2", 
            0, 0, "USER", false
        );
        
        return jdbcTemplate.queryForObject(
            "SELECT user_id FROM user WHERE email = ?", 
            Long.class, 
            email
        );
    }
} 