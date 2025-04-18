package com.newbit.newbitfeatureservice.like.integration;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.like.dto.response.ColumnLikeResponse;
import com.newbit.newbitfeatureservice.like.service.ColumnLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DisplayName("칼럼 좋아요 서비스 통합 테스트")
class ColumnLikeServiceIntegrationTest {

    @Autowired
    private ColumnLikeService columnLikeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long testColumnId;
    private Long testUserId;
    private Long authorId;
    private String uniqueColumnTitle;
    private String uniqueEmail1;
    private String uniqueEmail2;

    @BeforeEach
    void setUp() {
        try {
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            uniqueColumnTitle = "통합테스트 칼럼 " + uniqueId;
            uniqueEmail1 = "test1_" + uniqueId + "@example.com";
            uniqueEmail2 = "test2_" + uniqueId + "@example.com";
            
            createTestUsers();
            
            jdbcTemplate.update(
                "INSERT INTO mentor (user_id, temperature, preferred_time, is_active, price, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())",
                authorId, 75.0, "09:00-12:00", true, 1000
            );
            
            Long mentorId = jdbcTemplate.queryForObject(
                "SELECT mentor_id FROM mentor WHERE user_id = ?", 
                Long.class, 
                authorId
            );
            
            jdbcTemplate.update(
                "INSERT INTO `column` (title, content, is_public, price, like_count, mentor_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())",
                uniqueColumnTitle, "테스트 내용입니다.", true, 0, 0, mentorId
            );
            
            testColumnId = jdbcTemplate.queryForObject(
                "SELECT column_id FROM `column` WHERE title = ?", 
                Long.class, 
                uniqueColumnTitle
            );
        } catch (Exception e) {
            System.err.println("테스트 데이터 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private void createTestUsers() {
        try {
            jdbcTemplate.update(
                "INSERT INTO user (email, nickname, password, phone_number, user_name, point, diamond, authority, is_suspended, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                uniqueEmail1, "테스트유저1", "password", "01012345678", "테스트유저1", 
                0, 0, "USER", false
            );
            
            jdbcTemplate.update(
                "INSERT INTO user (email, nickname, password, phone_number, user_name, point, diamond, authority, is_suspended, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                uniqueEmail2, "테스트유저2", "password", "01087654321", "테스트유저2", 
                0, 0, "USER", false
            );
            
            testUserId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM user WHERE email = ?", 
                Long.class, 
                uniqueEmail1
            );
            
            authorId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM user WHERE email = ?", 
                Long.class, 
                uniqueEmail2
            );
        } catch (Exception e) {
            System.err.println("테스트 사용자 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("테스트 사용자 설정 중 오류 발생", e);
        }
    }

    @Nested
    @DisplayName("좋아요 Toggle 통합 테스트")
    class ToggleLikeIntegrationTest {

        @Test
        @DisplayName("칼럼 좋아요 생성 및 취소 통합 테스트")
        void toggleLikeIntegrationTest() {
            // Given
            int initialLikeCount = columnLikeService.getLikeCount(testColumnId);
            boolean initialLikeStatus = columnLikeService.isLiked(testColumnId, testUserId);
            assertThat(initialLikeStatus).isFalse();
            
            // When - 좋아요 추가
            ColumnLikeResponse addResponse = columnLikeService.toggleLike(testColumnId, testUserId);
            
            // Then - 좋아요 추가 검증
            assertThat(addResponse).isNotNull();
            assertThat(addResponse.isLiked()).isTrue();
            assertThat(addResponse.getTotalLikes()).isEqualTo(initialLikeCount + 1);
            
            // DB에서 좋아요 상태 확인
            boolean likeStatus = columnLikeService.isLiked(testColumnId, testUserId);
            assertThat(likeStatus).isTrue();
            
            // DB에서 칼럼 좋아요 수 확인
            int updatedLikeCount = columnLikeService.getLikeCount(testColumnId);
            assertThat(updatedLikeCount).isEqualTo(initialLikeCount + 1);
            
            // When - 좋아요 취소
            ColumnLikeResponse cancelResponse = columnLikeService.toggleLike(testColumnId, testUserId);
            
            // Then - 좋아요 취소 검증
            assertThat(cancelResponse).isNotNull();
            assertThat(cancelResponse.isLiked()).isFalse();
            assertThat(cancelResponse.getTotalLikes()).isEqualTo(initialLikeCount);
            
            // DB에서 좋아요 상태 다시 확인
            boolean updatedLikeStatus = columnLikeService.isLiked(testColumnId, testUserId);
            assertThat(updatedLikeStatus).isFalse();
            
            // DB에서 칼럼 좋아요 수 다시 확인
            int finalLikeCount = columnLikeService.getLikeCount(testColumnId);
            assertThat(finalLikeCount).isEqualTo(initialLikeCount);
        }
        
        @Test
        @DisplayName("존재하지 않는 칼럼에 좋아요 시도 시 예외 발생")
        void toggleLikeNonExistingColumn() {
            // Given
            Long nonExistingColumnId = 9999L;
            
            // When & Then
            assertThrows(BusinessException.class, () -> 
                columnLikeService.toggleLike(nonExistingColumnId, testUserId)
            );
        }
    }
    
    @Nested
    @DisplayName("좋아요 조회 통합 테스트")
    class LikeInfoIntegrationTest {
        
        @Test
        @DisplayName("칼럼 좋아요 여부 확인 테스트")
        void isLikedIntegrationTest() {
            // Given
            assertThat(columnLikeService.isLiked(testColumnId, testUserId)).isFalse();
            
            // When
            columnLikeService.toggleLike(testColumnId, testUserId);
            
            // Then
            assertThat(columnLikeService.isLiked(testColumnId, testUserId)).isTrue();
        }
        
        @Test
        @DisplayName("칼럼 좋아요 수 확인 테스트")
        void getLikeCountIntegrationTest() {
            // Given
            int initialCount = columnLikeService.getLikeCount(testColumnId);
            
            // When
            columnLikeService.toggleLike(testColumnId, testUserId);
            
            // Then
            assertThat(columnLikeService.getLikeCount(testColumnId)).isEqualTo(initialCount + 1);
        }
    }
} 