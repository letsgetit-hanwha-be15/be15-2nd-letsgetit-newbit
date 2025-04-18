package com.newbit.newbitfeatureservice.report.query.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportTypeDTO;
import com.newbit.newbitfeatureservice.report.query.service.ReportQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportQueryService reportQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReportDTO report1;
    private ReportDTO report2;
    private ReportTypeDTO reportType1;
    private ReportTypeDTO reportType2;
    private Pageable pageable;
    private Page<ReportDTO> singleReportPage;
    private Page<ReportDTO> multipleReportPage;

    @BeforeEach
    void setUp() {
        reportType1 = new ReportTypeDTO(1L, "SPAM");
        reportType2 = new ReportTypeDTO(2L, "ABUSE");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        
        report1 = new ReportDTO(1L, 100L, null, 1L, reportType1, "Post content 1", ReportStatus.SUBMITTED, now, null);
        report2 = new ReportDTO(2L, 101L, 1L, null, reportType2, "Comment content 2", ReportStatus.SUBMITTED, yesterday, now);
        
        // 공통 페이징 설정
        pageable = PageRequest.of(0, 10);
        
        // 자주 사용되는 페이지 객체
        singleReportPage = new PageImpl<>(Collections.singletonList(report1), pageable, 1);
        multipleReportPage = new PageImpl<>(Arrays.asList(report1, report2), pageable, 2);
    }

    @Test
    @DisplayName("상태와 페이징으로 신고 목록 조회 성공")
    void getReportsWithStatusAndPaging() throws Exception {
        // given
        ReportStatus status = ReportStatus.SUBMITTED;
        
        given(reportQueryService.findReports(eq(status), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports")
                        .param("status", status.name())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].reportId").value(report1.getReportId()))
                .andExpect(jsonPath("$.content[0].reportType.reportTypeId").value(report1.getReportType().getReportTypeId()))
                .andExpect(jsonPath("$.content[0].reportType.reportTypeName").value(report1.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$.content[0].status").value(status.name()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("페이징으로 모든 상태의 신고 목록 조회 성공")
    void getReportsWithPagingOnly() throws Exception {
        // given
        given(reportQueryService.findReports(eq(null), any(Pageable.class))).willReturn(multipleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].reportType.reportTypeName").value(report1.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$.content[1].reportType.reportTypeName").value(report2.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("페이징 없이 모든 신고 목록 조회 성공")
    void getAllReportsWithoutPaging() throws Exception {
        // given
        List<ReportDTO> reportList = Arrays.asList(report1, report2);
        given(reportQueryService.findAllReportsWithoutPaging()).willReturn(reportList);

        // when & then
        mockMvc.perform(get("/api/v1/reports/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].reportId").value(report1.getReportId()))
                .andExpect(jsonPath("$[0].reportType.reportTypeName").value(report1.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$[1].reportId").value(report2.getReportId()))
                .andExpect(jsonPath("$[1].reportType.reportTypeName").value(report2.getReportType().getReportTypeName()));
    }
    
    @Test
    @DisplayName("게시글 ID로 신고 목록 조회 성공")
    void getReportsByPostId() throws Exception {
        // given
        Long postId = 1L;
        given(reportQueryService.findReportsByPostId(eq(postId), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/post/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].reportId").value(report1.getReportId()))
                .andExpect(jsonPath("$.content[0].postId").value(report1.getPostId()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("댓글 ID로 신고 목록 조회 성공")
    void getReportsByCommentId() throws Exception {
        // given
        Long commentId = 1L;
        given(reportQueryService.findReportsByCommentId(eq(commentId), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/comment/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("신고자 ID로 신고 목록 조회 성공")
    void getReportsByReporterId() throws Exception {
        // given
        Long userId = 100L;
        given(reportQueryService.findReportsByReporterId(eq(userId), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/reporter/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].userId").value(report1.getUserId()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("신고 유형별 신고 목록 조회 성공")
    void getReportsByReportTypeId() throws Exception {
        // given
        Long reportTypeId = 1L;
        given(reportQueryService.findReportsByReportTypeId(eq(reportTypeId), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/type/{reportTypeId}", reportTypeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].reportType.reportTypeId").value(reportType1.getReportTypeId()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("게시글 작성자 ID로 신고 목록 조회 성공")
    void getReportsByPostUserId() throws Exception {
        // given
        Long userId = 200L;
        given(reportQueryService.findReportsByPostUserId(eq(userId), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/post-user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("댓글 작성자 ID로 신고 목록 조회 성공")
    void getReportsByCommentUserId() throws Exception {
        // given
        Long userId = 201L;
        given(reportQueryService.findReportsByCommentUserId(eq(userId), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/comment-user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("게시글 또는 댓글 작성자 ID로 신고 목록 조회 성공 (통합)")
    void getReportsByContentUserId() throws Exception {
        // given
        Long userId = 202L;
        given(reportQueryService.findReportsByContentUserId(eq(userId), any(Pageable.class))).willReturn(multipleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/content-user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
    
    @Test
    @DisplayName("상태와 신고 유형으로 필터링된 신고 목록 조회 성공")
    void getReportsByStatusAndType() throws Exception {
        // given
        ReportStatus status = ReportStatus.SUBMITTED;
        Long reportTypeId = 1L;
        given(reportQueryService.findReportsByStatusAndReportTypeId(eq(status), eq(reportTypeId), any(Pageable.class)))
            .willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/filter")
                        .param("status", status.name())
                        .param("reportTypeId", reportTypeId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value(status.name()))
                .andExpect(jsonPath("$.content[0].reportType.reportTypeId").value(reportType1.getReportTypeId()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("상태로만 필터링된 신고 목록 조회 성공")
    void getReportsByStatusOnly() throws Exception {
        // given
        ReportStatus status = ReportStatus.SUBMITTED;
        given(reportQueryService.findReports(eq(status), any(Pageable.class))).willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/filter")
                        .param("status", status.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value(status.name()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("신고 유형으로만 필터링된 신고 목록 조회 성공")
    void getReportsByTypeOnly() throws Exception {
        // given
        Long reportTypeId = 1L;
        given(reportQueryService.findReportsByReportTypeId(eq(reportTypeId), any(Pageable.class)))
            .willReturn(singleReportPage);

        // when & then
        mockMvc.perform(get("/api/v1/reports/filter")
                        .param("reportTypeId", reportTypeId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].reportType.reportTypeId").value(reportType1.getReportTypeId()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
} 