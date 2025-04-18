package com.newbit.newbitfeatureservice.report.command.integration;

import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.newbitfeatureservice.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.newbitfeatureservice.report.command.application.service.ReportCommandService;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.Report;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportType;
import com.newbit.newbitfeatureservice.report.command.domain.repository.ReportRepository;
import com.newbit.newbitfeatureservice.report.command.domain.repository.ReportTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// 통합 테스트 Disabled
@Disabled
@SpringBootTest
@Transactional
class ReportIntegrationTest {

    @Autowired
    private ReportCommandService reportCommandService;

    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ReportTypeRepository reportTypeRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Long testPostId;
    private Long testUserId = 1L;
    private Long testCategoryId = 1L;
    private Long testCommentId;
    private ReportType testReportType;
    
    @BeforeEach
    void setUp() {
        System.out.println("===== 테스트 게시글 삽입 시작 =====");
        try {
            System.out.println("SQL 실행: INSERT INTO post (user_id, post_category_id, title, content, is_notice) VALUES (" 
                    + testUserId + ", " + testCategoryId + ", '테스트 게시글', '테스트 내용입니다.', false)");
            
            jdbcTemplate.update(
                "INSERT INTO post (user_id, post_category_id, title, content, is_notice) VALUES (?, ?, ?, ?, ?)",
                testUserId, testCategoryId, "테스트 게시글", "테스트 내용입니다.", false
            );
            
            testPostId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            System.out.println("게시글 삽입 성공. 생성된 ID: " + testPostId);
            
            System.out.println("SQL 실행: INSERT INTO comment (user_id, post_id, content, deleted_at) VALUES ("
                    + testUserId + ", " + testPostId + ", '테스트 댓글입니다.', null)");
            
            jdbcTemplate.update(
                "INSERT INTO comment (user_id, post_id, content, deleted_at) VALUES (?, ?, ?, ?)",
                testUserId, testPostId, "테스트 댓글입니다.", null
            );
            
            testCommentId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            System.out.println("댓글 삽입 성공. 생성된 ID: " + testCommentId);
            
            int reportTypeCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "report_type", "report_type_id = 1");
            System.out.println("신고 유형 확인: " + (reportTypeCount > 0 ? "이미 존재함" : "존재하지 않음"));
            
            if (reportTypeCount == 0) {
                System.out.println("SQL 실행: INSERT INTO report_type (report_type_id, name, description) VALUES (1, 'SPAM', '스팸 신고')");
                jdbcTemplate.update(
                    "INSERT INTO report_type (report_type_id, name, description) VALUES (?, ?, ?)",
                    1, "SPAM", "스팸 신고"
                );
                System.out.println("신고 유형 추가 완료");
            }
            
            testReportType = reportTypeRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("테스트에 필요한 신고 유형이 DB에 없습니다. ID: 1"));
            System.out.println("신고 유형 가져오기 완료: " + testReportType.getName());
            
            System.out.println("===== setUp 완료 =====");
        } catch (Exception e) {
            System.err.println("===== setUp 중 오류 발생 =====");
            System.err.println("오류 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @DisplayName("게시글 신고 생성 통합 테스트 - 실제 DB 저장 확인")
    void createPostReportIntegrationTest() {
        System.out.println("\n\n===== 게시글 신고 생성 테스트 시작 =====");
        
        // Given
        Long reportTypeId = 1L;
        String content = "이 게시글은 스팸입니다.";

        ReportCreateRequest request = new ReportCreateRequest(testUserId, testPostId, reportTypeId, content);
        System.out.println("신고 생성 요청 준비: userId=" + testUserId + ", postId=" + testPostId + ", reportTypeId=" + reportTypeId);

        // When
        System.out.println("신고 생성 서비스 호출");
        ReportCommandResponse response = reportCommandService.createPostReport(request);
        System.out.println("신고 생성 완료: reportId=" + response.getReportId());

        // Then
        System.out.println("===== 응답 검증 시작 =====");
        // 1. 응답 값 검증
        assertThat(response).isNotNull();
        System.out.println("응답이 null이 아님 검증 완료");
        
        assertThat(response.getReportId()).isNotNull();
        System.out.println("reportId가 null이 아님 검증 완료: " + response.getReportId());
        
        assertThat(response.getUserId()).isEqualTo(testUserId);
        System.out.println("userId 일치 검증 완료: " + response.getUserId());
        
        assertThat(response.getPostId()).isEqualTo(testPostId);
        System.out.println("postId 일치 검증 완료: " + response.getPostId());
        
        assertThat(response.getReportTypeId()).isEqualTo(reportTypeId);
        System.out.println("reportTypeId 일치 검증 완료: " + response.getReportTypeId());
        
        assertThat(response.getContent()).isEqualTo(content);
        System.out.println("content 일치 검증 완료");
        
        assertThat(response.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        System.out.println("status 일치 검증 완료: " + response.getStatus());
        
        System.out.println("===== 응답 검증 완료 =====");
        
        // 2. DB에서 직접 조회하여 저장 여부 확인
        System.out.println("===== DB 저장 검증 시작 =====");
        Report savedReport = reportRepository.findById(response.getReportId());
        System.out.println("DB에서 신고 조회 완료: " + (savedReport != null ? "성공" : "실패"));
        
        assertThat(savedReport).isNotNull();
        System.out.println("저장된 신고가 null이 아님 검증 완료");
        
        assertThat(savedReport.getUserId()).isEqualTo(testUserId);
        System.out.println("저장된 userId 일치 검증 완료: " + savedReport.getUserId());
        
        assertThat(savedReport.getPostId()).isEqualTo(testPostId);
        System.out.println("저장된 postId 일치 검증 완료: " + savedReport.getPostId());
        
        assertThat(savedReport.getReportTypeId()).isEqualTo(reportTypeId);
        System.out.println("저장된 reportTypeId 일치 검증 완료: " + savedReport.getReportTypeId());
        
        assertThat(savedReport.getContent()).isEqualTo(content);
        System.out.println("저장된 content 일치 검증 완료");
        
        assertThat(savedReport.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        System.out.println("저장된 status 일치 검증 완료: " + savedReport.getStatus());
        
        System.out.println("===== DB 저장 검증 완료 =====");
        System.out.println("===== 테스트 성공적으로 완료 =====\n\n");
    }

    @Test
    @DisplayName("댓글 신고 생성 통합 테스트 - 실제 DB 저장 확인")
    void createCommentReportIntegrationTest() {
        System.out.println("\n\n===== 댓글 신고 생성 테스트 시작 =====");
        
        // Given
        Long reportTypeId = 1L;
        String content = "이 댓글은 스팸입니다.";

        ReportCreateRequest request = new ReportCreateRequest(testUserId, testCommentId, reportTypeId, content, true);
        System.out.println("신고 생성 요청 준비: userId=" + testUserId + ", commentId=" + testCommentId + ", reportTypeId=" + reportTypeId);

        // When
        System.out.println("신고 생성 서비스 호출");
        ReportCommandResponse response = reportCommandService.createCommentReport(request);
        System.out.println("신고 생성 완료: reportId=" + response.getReportId());

        // Then
        System.out.println("===== 응답 검증 시작 =====");
        // 1. 응답 값 검증
        assertThat(response).isNotNull();
        System.out.println("응답이 null이 아님 검증 완료");
        
        assertThat(response.getReportId()).isNotNull();
        System.out.println("reportId가 null이 아님 검증 완료: " + response.getReportId());
        
        assertThat(response.getUserId()).isEqualTo(testUserId);
        System.out.println("userId 일치 검증 완료: " + response.getUserId());
        
        assertThat(response.getCommentId()).isEqualTo(testCommentId);
        System.out.println("commentId 일치 검증 완료: " + response.getCommentId());
        
        assertThat(response.getReportTypeId()).isEqualTo(reportTypeId);
        System.out.println("reportTypeId 일치 검증 완료: " + response.getReportTypeId());
        
        assertThat(response.getContent()).isEqualTo(content);
        System.out.println("content 일치 검증 완료");
        
        assertThat(response.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        System.out.println("status 일치 검증 완료: " + response.getStatus());
        
        System.out.println("===== 응답 검증 완료 =====");
        
        // 2. DB에서 직접 조회하여 저장 여부 확인
        System.out.println("===== DB 저장 검증 시작 =====");
        Report savedReport = reportRepository.findById(response.getReportId());
        System.out.println("DB에서 신고 조회 완료: " + (savedReport != null ? "성공" : "실패"));
        
        assertThat(savedReport).isNotNull();
        System.out.println("저장된 신고가 null이 아님 검증 완료");
        
        assertThat(savedReport.getUserId()).isEqualTo(testUserId);
        System.out.println("저장된 userId 일치 검증 완료: " + savedReport.getUserId());
        
        assertThat(savedReport.getCommentId()).isEqualTo(testCommentId);
        System.out.println("저장된 commentId 일치 검증 완료: " + savedReport.getCommentId());
        
        assertThat(savedReport.getReportTypeId()).isEqualTo(reportTypeId);
        System.out.println("저장된 reportTypeId 일치 검증 완료: " + savedReport.getReportTypeId());
        
        assertThat(savedReport.getContent()).isEqualTo(content);
        System.out.println("저장된 content 일치 검증 완료");
        
        assertThat(savedReport.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        System.out.println("저장된 status 일치 검증 완료: " + savedReport.getStatus());
        
        System.out.println("===== DB 저장 검증 완료 =====");
        System.out.println("===== 테스트 성공적으로 완료 =====\n\n");
    }
} 