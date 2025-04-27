package com.newbit.newbitfeatureservice.report.query.service;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.domain.repository.ReportQueryRepository;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.service.CommentService;
import com.newbit.newbitfeatureservice.post.service.PostService;
import com.newbit.newbitfeatureservice.report.query.dto.ReportedCommentResponse;
import com.newbit.newbitfeatureservice.report.query.dto.ReportedPostResponse;

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceTest {

    @InjectMocks
    private ReportQueryService reportQueryService;

    @Mock
    private ReportQueryRepository reportQueryRepository;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    private ReportDTO report1_post1, report2_post1, report3_post2;
    private ReportDTO report4_comment1, report5_comment1, report6_comment2;
    private ReportTypeDTO reportTypeDto1;
    private Pageable pageable;
    private Page<ReportDTO> singleReportPage;
    private Page<ReportDTO> multipleReportPage;

    @BeforeEach
    void setUp() {
        reportTypeDto1 = new ReportTypeDTO(1L, "Spam");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        
        // Reports for posts
        report1_post1 = new ReportDTO(1L, 100L, null, 1L, reportTypeDto1, "First report post 1", ReportStatus.SUBMITTED, now.minusDays(2), null);
        report2_post1 = new ReportDTO(2L, 101L, null, 1L, reportTypeDto1, "Second report post 1", ReportStatus.SUBMITTED, now.minusDays(1), null);
        report3_post2 = new ReportDTO(3L, 102L, null, 2L, reportTypeDto1, "First report post 2", ReportStatus.SUBMITTED, now, null);

        // Reports for comments
        report4_comment1 = new ReportDTO(4L, 103L, 10L, null, reportTypeDto1, "First report comment 1", ReportStatus.SUBMITTED, now.minusHours(2), null);
        report5_comment1 = new ReportDTO(5L, 104L, 10L, null, reportTypeDto1, "Second report comment 1", ReportStatus.SUBMITTED, now.minusHours(1), null);
        report6_comment2 = new ReportDTO(6L, 105L, 11L, null, reportTypeDto1, "First report comment 2", ReportStatus.SUBMITTED, now, null);
        
        // 공통 페이징 설정
        pageable = PageRequest.of(0, 10);
        
        // 자주 사용되는 페이지 객체
        singleReportPage = new PageImpl<>(Collections.singletonList(report1_post1), pageable, 1);
        multipleReportPage = new PageImpl<>(Arrays.asList(report1_post1, report2_post1, report3_post2, report4_comment1, report5_comment1, report6_comment2), pageable, 6);
    }

    @Test
    @DisplayName("상태와 페이징으로 신고 목록 조회")
    void findReportsWithStatusAndPaging() {
        // given
        ReportStatus status = ReportStatus.SUBMITTED;
        
        given(reportQueryRepository.findReports(eq(status), eq(pageable))).willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReports(status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        ReportDTO resultDto = result.getContent().get(0);
        assertThat(resultDto.getReportId()).isEqualTo(report1_post1.getReportId());
        assertThat(resultDto.getReportType().getReportTypeId()).isEqualTo(reportTypeDto1.getReportTypeId());
        assertThat(resultDto.getReportType().getReportTypeName()).isEqualTo(reportTypeDto1.getReportTypeName());
        assertThat(resultDto.getStatus()).isEqualTo(status);

        then(reportQueryRepository).should(times(1)).findReports(eq(status), eq(pageable));
    }

    @Test
    @DisplayName("페이징으로 모든 상태의 신고 목록 조회")
    void findReportsWithPagingOnly() {
        // given
        given(reportQueryRepository.findReports(eq(null), eq(pageable))).willReturn(multipleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReports(null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(6);
        assertThat(result.getTotalElements()).isEqualTo(6);

        assertThat(result.getContent().get(0).getReportId()).isEqualTo(report1_post1.getReportId());
        assertThat(result.getContent().get(1).getReportId()).isEqualTo(report2_post1.getReportId());
        assertThat(result.getContent().get(2).getReportId()).isEqualTo(report3_post2.getReportId());
        assertThat(result.getContent().get(3).getReportId()).isEqualTo(report4_comment1.getReportId());
        assertThat(result.getContent().get(4).getReportId()).isEqualTo(report5_comment1.getReportId());
        assertThat(result.getContent().get(5).getReportId()).isEqualTo(report6_comment2.getReportId());

        then(reportQueryRepository).should(times(1)).findReports(eq(null), eq(pageable));
    }

    @Test
    @DisplayName("페이징 없이 모든 신고 목록 조회")
    void findAllReportsWithoutPaging() {
        // given
        List<ReportDTO> reportDtoList = Arrays.asList(report1_post1, report2_post1, report3_post2, report4_comment1, report5_comment1, report6_comment2);
        
        given(reportQueryRepository.findAllWithoutPaging()).willReturn(reportDtoList);

        // when
        List<ReportDTO> result = reportQueryService.findAllReportsWithoutPaging();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(6);
        
        assertThat(result.get(0).getReportId()).isEqualTo(report1_post1.getReportId());
        assertThat(result.get(1).getReportId()).isEqualTo(report2_post1.getReportId());
        assertThat(result.get(2).getReportId()).isEqualTo(report3_post2.getReportId());
        assertThat(result.get(3).getReportId()).isEqualTo(report4_comment1.getReportId());
        assertThat(result.get(4).getReportId()).isEqualTo(report5_comment1.getReportId());
        assertThat(result.get(5).getReportId()).isEqualTo(report6_comment2.getReportId());

        then(reportQueryRepository).should(times(1)).findAllWithoutPaging();
    }
    
    @Test
    @DisplayName("게시글 ID로 신고 목록 조회")
    void findReportsByPostId() {
        // given
        Long postId = 1L;
        given(reportQueryRepository.findReportsByPostId(eq(postId), eq(pageable))).willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByPostId(postId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getPostId()).isEqualTo(postId);
        
        then(reportQueryRepository).should(times(1)).findReportsByPostId(eq(postId), eq(pageable));
    }
    
    @Test
    @DisplayName("댓글 ID로 신고 목록 조회")
    void findReportsByCommentId() {
        // given
        Long commentId = 10L;
        given(reportQueryRepository.findReportsByCommentId(eq(commentId), eq(pageable))).willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByCommentId(commentId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        then(reportQueryRepository).should(times(1)).findReportsByCommentId(eq(commentId), eq(pageable));
    }
    
    @Test
    @DisplayName("신고자 ID로 신고 목록 조회")
    void findReportsByReporterId() {
        // given
        Long reporterId = 100L;
        given(reportQueryRepository.findReportsByReporterId(eq(reporterId), eq(pageable))).willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByReporterId(reporterId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(reporterId);
        
        then(reportQueryRepository).should(times(1)).findReportsByReporterId(eq(reporterId), eq(pageable));
    }
    
    @Test
    @DisplayName("신고 유형 ID로 신고 목록 조회")
    void findReportsByReportTypeId() {
        // given
        Long reportTypeId = 1L;
        given(reportQueryRepository.findReportsByReportTypeId(eq(reportTypeId), eq(pageable))).willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByReportTypeId(reportTypeId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getReportType().getReportTypeId()).isEqualTo(reportTypeId);
        
        then(reportQueryRepository).should(times(1)).findReportsByReportTypeId(eq(reportTypeId), eq(pageable));
    }
    
    @Test
    @DisplayName("상태와 신고 유형 ID로 신고 목록 조회")
    void findReportsByStatusAndReportTypeId() {
        // given
        Long reportTypeId = 1L;
        ReportStatus status = ReportStatus.SUBMITTED;
        given(reportQueryRepository.findReportsByStatusAndReportTypeId(eq(status), eq(reportTypeId), eq(pageable)))
            .willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByStatusAndReportTypeId(status, reportTypeId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(status);
        assertThat(result.getContent().get(0).getReportType().getReportTypeId()).isEqualTo(reportTypeId);
        
        then(reportQueryRepository).should(times(1))
            .findReportsByStatusAndReportTypeId(eq(status), eq(reportTypeId), eq(pageable));
    }
    
    @Test
    @DisplayName("게시글 작성자 ID로 신고 목록 조회")
    void findReportsByPostUserId() {
        // given
        Long userId = 100L; // 게시글 작성자 ID
        given(reportQueryRepository.findReportsByPostUserId(eq(userId), eq(pageable))).willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByPostUserId(userId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        then(reportQueryRepository).should(times(1)).findReportsByPostUserId(eq(userId), eq(pageable));
    }
    
    @Test
    @DisplayName("댓글 작성자 ID로 신고 목록 조회")
    void findReportsByCommentUserId() {
        // given
        Long userId = 10L; // 댓글 작성자 ID
        given(reportQueryRepository.findReportsByCommentUserId(eq(userId), eq(pageable))).willReturn(singleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByCommentUserId(userId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        then(reportQueryRepository).should(times(1)).findReportsByCommentUserId(eq(userId), eq(pageable));
    }
    
    @Test
    @DisplayName("게시글 또는 댓글 작성자 ID로 신고 목록 조회 (통합)")
    void findReportsByContentUserId() {
        // given
        Long userId = 10L; // 콘텐츠 작성자 ID
        given(reportQueryRepository.findReportsByContentUserId(eq(userId), eq(pageable))).willReturn(multipleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByContentUserId(userId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(6);
        assertThat(result.getTotalElements()).isEqualTo(6);
        
        then(reportQueryRepository).should(times(1)).findReportsByContentUserId(eq(userId), eq(pageable));
    }

    @Test
    @DisplayName("신고된 게시글 목록 조회 - 성공")
    void findReportedPosts_Success() {
        // Given
        List<ReportDTO> mockReports = List.of(report1_post1, report2_post1, report3_post2, report4_comment1); 
        when(reportQueryRepository.findAllWithoutPaging()).thenReturn(mockReports);
        when(postService.getPostTitle(1L)).thenReturn("Post Title 1");
        when(postService.getPostTitle(2L)).thenReturn("Post Title 2");

        // When
        List<ReportedPostResponse> result = reportQueryService.findReportedPosts();

        // Then
        assertThat(result).hasSize(2);
        
        // Post 1 (should have 2 reports, sorted first)
        ReportedPostResponse post1Result = result.get(0);
        assertThat(post1Result.getPostId()).isEqualTo(1L);
        assertThat(post1Result.getPostTitle()).isEqualTo("Post Title 1");
        assertThat(post1Result.getReportCount()).isEqualTo(2L);
        assertThat(post1Result.getLastReportContent()).isEqualTo("Second report post 1");

        // Post 2 (should have 1 report)
        ReportedPostResponse post2Result = result.get(1);
        assertThat(post2Result.getPostId()).isEqualTo(2L);
        assertThat(post2Result.getPostTitle()).isEqualTo("Post Title 2");
        assertThat(post2Result.getReportCount()).isEqualTo(1L);
        assertThat(post2Result.getLastReportContent()).isEqualTo("First report post 2");
    }
    
    @Test
    @DisplayName("신고된 게시글 목록 조회 - 게시글 제목 조회 실패")
    void findReportedPosts_PostNotFound() {
        // Given
        List<ReportDTO> mockReports = List.of(report1_post1);
        when(reportQueryRepository.findAllWithoutPaging()).thenReturn(mockReports);
        when(postService.getPostTitle(1L)).thenThrow(new BusinessException(ErrorCode.POST_NOT_FOUND));

        // When
        List<ReportedPostResponse> result = reportQueryService.findReportedPosts();

        // Then
        assertThat(result).hasSize(1);
        ReportedPostResponse post1Result = result.get(0);
        assertThat(post1Result.getPostId()).isEqualTo(1L);
        assertThat(post1Result.getPostTitle()).isEqualTo("Post Not Found");
        assertThat(post1Result.getReportCount()).isEqualTo(1L);
        assertThat(post1Result.getLastReportContent()).isEqualTo("First report post 1");
    }

    @Test
    @DisplayName("신고된 댓글 목록 조회 - 성공")
    void findReportedComments_Success() {
        // Given
        List<ReportDTO> mockReports = List.of(report1_post1, report4_comment1, report5_comment1, report6_comment2); 
        when(reportQueryRepository.findAllWithoutPaging()).thenReturn(mockReports);
        when(commentService.getCommentContent(10L)).thenReturn("Comment Content 10");
        when(commentService.getCommentContent(11L)).thenReturn("Comment Content 11");

        // When
        List<ReportedCommentResponse> result = reportQueryService.findReportedComments();

        // Then
        assertThat(result).hasSize(2);

        // Comment 1 (should have 2 reports, sorted first)
        ReportedCommentResponse comment10Result = result.get(0);
        assertThat(comment10Result.getCommentId()).isEqualTo(10L);
        assertThat(comment10Result.getCommentContent()).isEqualTo("Comment Content 10");
        assertThat(comment10Result.getReportCount()).isEqualTo(2L);
        assertThat(comment10Result.getLastReportContent()).isEqualTo("Second report comment 1");

        // Comment 2 (should have 1 report)
        ReportedCommentResponse comment11Result = result.get(1);
        assertThat(comment11Result.getCommentId()).isEqualTo(11L);
        assertThat(comment11Result.getCommentContent()).isEqualTo("Comment Content 11");
        assertThat(comment11Result.getReportCount()).isEqualTo(1L);
        assertThat(comment11Result.getLastReportContent()).isEqualTo("First report comment 2");
    }
    
    @Test
    @DisplayName("신고된 댓글 목록 조회 - 댓글 내용 조회 실패")
    void findReportedComments_CommentNotFound() {
        // Given
        List<ReportDTO> mockReports = List.of(report4_comment1);
        when(reportQueryRepository.findAllWithoutPaging()).thenReturn(mockReports);
        when(commentService.getCommentContent(10L)).thenThrow(new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // When
        List<ReportedCommentResponse> result = reportQueryService.findReportedComments();

        // Then
        assertThat(result).hasSize(1);
        ReportedCommentResponse comment10Result = result.get(0);
        assertThat(comment10Result.getCommentId()).isEqualTo(10L);
        assertThat(comment10Result.getCommentContent()).isEqualTo("Comment Not Found");
        assertThat(comment10Result.getReportCount()).isEqualTo(1L);
        assertThat(comment10Result.getLastReportContent()).isEqualTo("First report comment 1");
    }

    @Test
    @DisplayName("신고된 게시글 목록 조회 - 신고 내역 없음")
    void findReportedPosts_NoReports() {
        // Given
        when(reportQueryRepository.findAllWithoutPaging()).thenReturn(Collections.emptyList());

        // When
        List<ReportedPostResponse> result = reportQueryService.findReportedPosts();

        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("신고된 댓글 목록 조회 - 신고 내역 없음")
    void findReportedComments_NoReports() {
        // Given
        when(reportQueryRepository.findAllWithoutPaging()).thenReturn(Collections.emptyList());

        // When
        List<ReportedCommentResponse> result = reportQueryService.findReportedComments();

        // Then
        assertThat(result).isEmpty();
    }
} 