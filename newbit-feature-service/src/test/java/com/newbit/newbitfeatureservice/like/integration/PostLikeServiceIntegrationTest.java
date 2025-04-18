package com.newbit.newbitfeatureservice.like.integration;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.like.dto.response.PostLikeResponse;
import com.newbit.newbitfeatureservice.like.service.PostLikeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 실제 db에 데이터를 삽입하므로 Disabled 처리
@Disabled
@SpringBootTest
// 포인트 지급 확인을 위해 Transactional 주석 처리
//@Transactional
@DisplayName("게시글 좋아요 서비스 통합 테스트")
class PostLikeServiceIntegrationTest {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long testPostId;
    private Long testUserId;
    private Long authorId;
    private String uniquePostTitle;
    private String uniqueEmail1;
    private String uniqueEmail2;
    private Integer categoryId;

    @BeforeEach
    void setUp() {
        try {
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            uniquePostTitle = "통합테스트 게시글 " + uniqueId;
            uniqueEmail1 = "test1_" + uniqueId + "@example.com";
            uniqueEmail2 = "test2_" + uniqueId + "@example.com";
            
            createTestUsers();
            
            createOrGetTestCategory();
            
            createOrGetPointType();

            jdbcTemplate.update(
                "INSERT INTO post (user_id, post_category_id, title, content, is_notice, like_count, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())",
                authorId, categoryId, uniquePostTitle, "테스트 내용입니다.", false, 0
            );
            
            testPostId = jdbcTemplate.queryForObject(
                "SELECT post_id FROM post WHERE title = ?", 
                Long.class, 
                uniquePostTitle
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
    
    private void createOrGetTestCategory() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_category WHERE post_category_name = ?", 
                Integer.class,
                "테스트 카테고리"
            );
            
            if (count != null && count == 0) {
                jdbcTemplate.update(
                    "INSERT INTO post_category (post_category_name, created_at, updated_at) VALUES (?, NOW(), NOW())",
                    "테스트 카테고리"
                );
            }
            
            categoryId = jdbcTemplate.queryForObject(
                "SELECT post_category_id FROM post_category WHERE post_category_name = ?",
                Integer.class,
                "테스트 카테고리"
            );
        } catch (Exception e) {
            System.err.println("테스트 카테고리 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("테스트 카테고리 설정 중 오류 발생", e);
        }
    }
    
    private void createOrGetPointType() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM point_type WHERE point_type_name = ?",
                Integer.class,
                "게시글 좋아요"
            );
            
            if (count != null && count == 0) {
                jdbcTemplate.update(
                    "INSERT INTO point_type (point_type_name, increase_amount, decrease_amount) VALUES (?, ?, ?)",
                    "게시글 좋아요", 3, null
                );
            }
        } catch (Exception e) {
            System.err.println("포인트 타입 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("포인트 타입 설정 중 오류 발생", e);
        }
    }

    @Nested
    @DisplayName("좋아요 Toggle 통합 테스트")
    class ToggleLikeIntegrationTest {

        @Test
        @DisplayName("게시글 좋아요 생성 및 취소 통합 테스트")
        void toggleLikeIntegrationTest() {
            // Given
            int initialLikeCount = postLikeService.getLikeCount(testPostId);
            boolean initialLikeStatus = postLikeService.isLiked(testPostId, testUserId);
            assertThat(initialLikeStatus).isFalse();
            
            // When - 좋아요 추가
            PostLikeResponse addResponse = postLikeService.toggleLike(testPostId, testUserId);
            
            // Then - 좋아요 추가 검증
            assertThat(addResponse).isNotNull();
            assertThat(addResponse.isLiked()).isTrue();
            assertThat(addResponse.getTotalLikes()).isEqualTo(initialLikeCount + 1);
            
            // DB에서 좋아요 상태 확인
            boolean likeStatus = postLikeService.isLiked(testPostId, testUserId);
            assertThat(likeStatus).isTrue();
            
            // DB에서 게시글 좋아요 수 확인
            int updatedLikeCount = postLikeService.getLikeCount(testPostId);
            assertThat(updatedLikeCount).isEqualTo(initialLikeCount + 1);
            
            // When - 좋아요 취소
            PostLikeResponse cancelResponse = postLikeService.toggleLike(testPostId, testUserId);
            
            // Then - 좋아요 취소 검증
            assertThat(cancelResponse).isNotNull();
            assertThat(cancelResponse.isLiked()).isFalse();
            assertThat(cancelResponse.getTotalLikes()).isEqualTo(initialLikeCount);
            
            // DB에서 좋아요 상태 다시 확인
            boolean updatedLikeStatus = postLikeService.isLiked(testPostId, testUserId);
            assertThat(updatedLikeStatus).isFalse();
            
            // DB에서 게시글 좋아요 수 다시 확인
            int finalLikeCount = postLikeService.getLikeCount(testPostId);
            assertThat(finalLikeCount).isEqualTo(initialLikeCount);
        }
        
        @Test
        @DisplayName("존재하지 않는 게시글에 좋아요 시도 시 예외 발생")
        void toggleLikeNonExistingPost() {
            // Given
            Long nonExistingPostId = 9999L;
            
            // When & Then
            assertThrows(BusinessException.class, () -> 
                postLikeService.toggleLike(nonExistingPostId, testUserId)
            );
        }
    }
    
    @Nested
    @DisplayName("좋아요 조회 통합 테스트")
    class LikeInfoIntegrationTest {
        
        @Test
        @DisplayName("게시글 좋아요 여부 확인 테스트")
        void isLikedIntegrationTest() {
            // Given
            assertThat(postLikeService.isLiked(testPostId, testUserId)).isFalse();
            
            // When
            postLikeService.toggleLike(testPostId, testUserId);
            
            // Then
            assertThat(postLikeService.isLiked(testPostId, testUserId)).isTrue();
        }
        
        @Test
        @DisplayName("게시글 좋아요 수 확인 테스트")
        void getLikeCountIntegrationTest() {
            // Given
            int initialCount = postLikeService.getLikeCount(testPostId);
            
            // When
            postLikeService.toggleLike(testPostId, testUserId);
            
            // Then
            assertThat(postLikeService.getLikeCount(testPostId)).isEqualTo(initialCount + 1);
        }
    }
    
    @Nested
    @DisplayName("포인트 지급 통합 테스트")
    class PointRewardIntegrationTest {
        
        @Test
        @DisplayName("첫 좋아요 시 작성자에게 포인트 지급 테스트")
        void givePointOnFirstLikeIntegrationTest() {
            // Given
            int initialAuthorPoints = getAuthorPoints();
            
            // When
            postLikeService.toggleLike(testPostId, testUserId);
            
            // Then
            int updatedAuthorPoints = getAuthorPoints();
            assertThat(updatedAuthorPoints).isGreaterThan(initialAuthorPoints);
            
            // point_history 테이블에 내역이 생겼는지 확인
            int pointHistoryCount = getPointHistoryCount(authorId, testPostId);
            assertThat(pointHistoryCount).isGreaterThan(0);
        }
        
        @Test
        @DisplayName("두 번째 좋아요 시 포인트 미지급 테스트")
        void doNotGivePointOnSecondLikeIntegrationTest() {
            // Given
            // 첫 좋아요 후 취소
            postLikeService.toggleLike(testPostId, testUserId);
            postLikeService.toggleLike(testPostId, testUserId);
            
            int initialAuthorPoints = getAuthorPoints();
            
            // When - 다시 좋아요
            postLikeService.toggleLike(testPostId, testUserId);
            
            // Then - 포인트 변화가 없어야 함
            int updatedAuthorPoints = getAuthorPoints();
            assertThat(updatedAuthorPoints).isEqualTo(initialAuthorPoints);
        }
        
        private int getAuthorPoints() {
            try {
                Integer points = jdbcTemplate.queryForObject(
                    "SELECT point FROM user WHERE user_id = ?",
                    Integer.class,
                    authorId
                );
                return points != null ? points : 0;
            } catch (DataAccessException e) {
                return 0;
            }
        }

        private int getPointHistoryCount(Long userId, Long serviceId) {
            try {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM point_history WHERE user_id = ? AND service_id = ?",
                    Integer.class,
                    userId, serviceId
                );
                return count != null ? count : 0;
            } catch (DataAccessException e) {
                return 0;
            }
        }
    }
}