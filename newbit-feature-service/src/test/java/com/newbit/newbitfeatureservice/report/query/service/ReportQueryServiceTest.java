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

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceTest {

    @InjectMocks
    private ReportQueryService reportQueryService;

    @Mock
    private ReportQueryRepository reportQueryRepository;

    private ReportDTO reportDto1;
    private ReportDTO reportDto2;
    private ReportTypeDTO reportTypeDto1;
    private ReportTypeDTO reportTypeDto2;
    private Pageable pageable;
    private Page<ReportDTO> singleReportPage;
    private Page<ReportDTO> multipleReportPage;

    @BeforeEach
    void setUp() {
        reportTypeDto1 = new ReportTypeDTO(1L, "SPAM");
        reportTypeDto2 = new ReportTypeDTO(2L, "ABUSE");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        
        // ReportDTO 객체 직접 생성
        reportDto1 = new ReportDTO(1L, 100L, null, 1L, reportTypeDto1, "Post content 1", ReportStatus.SUBMITTED, now, null);
        reportDto2 = new ReportDTO(2L, 101L, 1L, null, reportTypeDto2, "Comment content 2", ReportStatus.SUBMITTED, yesterday, now);
        
        // 공통 페이징 설정
        pageable = PageRequest.of(0, 10);
        
        // 자주 사용되는 페이지 객체
        singleReportPage = new PageImpl<>(Collections.singletonList(reportDto1), pageable, 1);
        multipleReportPage = new PageImpl<>(Arrays.asList(reportDto1, reportDto2), pageable, 2);
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
        assertThat(resultDto.getReportId()).isEqualTo(reportDto1.getReportId());
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
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        assertThat(result.getContent().get(0).getReportId()).isEqualTo(reportDto1.getReportId());
        assertThat(result.getContent().get(1).getReportId()).isEqualTo(reportDto2.getReportId());
        assertThat(result.getContent().get(0).getReportType().getReportTypeName()).isEqualTo(reportTypeDto1.getReportTypeName());
        assertThat(result.getContent().get(1).getReportType().getReportTypeName()).isEqualTo(reportTypeDto2.getReportTypeName());

        then(reportQueryRepository).should(times(1)).findReports(eq(null), eq(pageable));
    }

    @Test
    @DisplayName("페이징 없이 모든 신고 목록 조회")
    void findAllReportsWithoutPaging() {
        // given
        List<ReportDTO> reportDtoList = Arrays.asList(reportDto1, reportDto2);
        
        given(reportQueryRepository.findAllWithoutPaging()).willReturn(reportDtoList);

        // when
        List<ReportDTO> result = reportQueryService.findAllReportsWithoutPaging();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        assertThat(result.get(0).getReportId()).isEqualTo(reportDto1.getReportId());
        assertThat(result.get(1).getReportId()).isEqualTo(reportDto2.getReportId());
        assertThat(result.get(0).getReportType().getReportTypeName()).isEqualTo(reportTypeDto1.getReportTypeName());
        assertThat(result.get(1).getReportType().getReportTypeName()).isEqualTo(reportTypeDto2.getReportTypeName());

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
        Long commentId = 1L;
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
        Long userId = 200L; // 게시글 작성자 ID
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
        Long userId = 201L; // 댓글 작성자 ID
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
        Long userId = 202L; // 콘텐츠 작성자 ID
        given(reportQueryRepository.findReportsByContentUserId(eq(userId), eq(pageable))).willReturn(multipleReportPage);

        // when
        Page<ReportDTO> result = reportQueryService.findReportsByContentUserId(userId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        
        then(reportQueryRepository).should(times(1)).findReportsByContentUserId(eq(userId), eq(pageable));
    }
} 