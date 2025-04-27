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
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.report.query.dto.ReportedCommentResponse;
import com.newbit.newbitfeatureservice.report.query.dto.ReportedPostResponse;

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
        
        pageable = PageRequest.of(0, 10);
        singleReportPage = new PageImpl<>(Collections.singletonList(report1), pageable, 1);
        multipleReportPage = new PageImpl<>(Arrays.asList(report1, report2), pageable, 2);
    }

    @Test
    @DisplayName("상태와 페이징으로 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsWithStatusAndPaging() throws Exception {
        ReportStatus status = ReportStatus.SUBMITTED;
        given(reportQueryService.findReports(eq(status), any(Pageable.class))).willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query")
                        .param("status", status.name())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].reportId").value(report1.getReportId()))
                .andExpect(jsonPath("$.data.content[0].reportType.reportTypeId").value(report1.getReportType().getReportTypeId()))
                .andExpect(jsonPath("$.data.content[0].reportType.reportTypeName").value(report1.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$.data.content[0].status").value(status.name()))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("페이징으로 모든 상태의 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsWithPagingOnly() throws Exception {
        given(reportQueryService.findReports(eq(null), any(Pageable.class))).willReturn(multipleReportPage);

        mockMvc.perform(get("/reports/query")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].reportType.reportTypeName").value(report1.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$.data.content[1].reportType.reportTypeName").value(report2.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @DisplayName("페이징 없이 모든 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getAllReportsWithoutPaging() throws Exception {
        List<ReportDTO> reportList = Arrays.asList(report1, report2);
        given(reportQueryService.findAllReportsWithoutPaging()).willReturn(reportList);

        mockMvc.perform(get("/reports/query/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].reportId").value(report1.getReportId()))
                .andExpect(jsonPath("$.data[0].reportType.reportTypeName").value(report1.getReportType().getReportTypeName()))
                .andExpect(jsonPath("$.data[1].reportId").value(report2.getReportId()))
                .andExpect(jsonPath("$.data[1].reportType.reportTypeName").value(report2.getReportType().getReportTypeName()));
    }
    
    @Test
    @DisplayName("게시글 ID로 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByPostId() throws Exception {
        Long postId = 1L;
        given(reportQueryService.findReportsByPostId(eq(postId), any(Pageable.class))).willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query/post/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].reportId").value(report1.getReportId()))
                .andExpect(jsonPath("$.data.content[0].postId").value(report1.getPostId()))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @DisplayName("댓글 ID로 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByCommentId() throws Exception {
        Long commentId = 1L;
        Page<ReportDTO> commentReportPage = new PageImpl<>(Collections.singletonList(report2), pageable, 1);
        given(reportQueryService.findReportsByCommentId(eq(commentId), any(Pageable.class))).willReturn(commentReportPage);

        mockMvc.perform(get("/reports/query/comment/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].reportId").value(report2.getReportId()));
    }
    
    @Test
    @DisplayName("신고자 ID로 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByReporterId() throws Exception {
        Long userId = 100L;
        given(reportQueryService.findReportsByReporterId(eq(userId), any(Pageable.class))).willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query/reporter/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].userId").value(report1.getUserId()))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @DisplayName("신고 유형별 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByReportTypeId() throws Exception {
        Long reportTypeId = 1L;
        given(reportQueryService.findReportsByReportTypeId(eq(reportTypeId), any(Pageable.class))).willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query/type/{reportTypeId}", reportTypeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].reportType.reportTypeId").value(reportType1.getReportTypeId()))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @DisplayName("게시글 작성자 ID로 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByPostUserId() throws Exception {
        Long userId = 200L;
        given(reportQueryService.findReportsByPostUserId(eq(userId), any(Pageable.class))).willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query/post-user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @DisplayName("댓글 작성자 ID로 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByCommentUserId() throws Exception {
        Long userId = 201L;
        Page<ReportDTO> commentUserReportPage = new PageImpl<>(Collections.singletonList(report2), pageable, 1); 
        given(reportQueryService.findReportsByCommentUserId(eq(userId), any(Pageable.class))).willReturn(commentUserReportPage);

        mockMvc.perform(get("/reports/query/comment-user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @DisplayName("게시글 또는 댓글 작성자 ID로 신고 목록 조회 성공 (통합)")
    @WithMockUser(roles = "ADMIN")
    void getReportsByContentUserId() throws Exception {
        Long userId = 202L;
        given(reportQueryService.findReportsByContentUserId(eq(userId), any(Pageable.class))).willReturn(multipleReportPage);

        mockMvc.perform(get("/reports/query/content-user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }
    
    @Test
    @DisplayName("상태와 신고 유형으로 필터링된 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByStatusAndType() throws Exception {
        ReportStatus status = ReportStatus.SUBMITTED;
        Long reportTypeId = 1L;
        given(reportQueryService.findReportsByStatusAndReportTypeId(eq(status), eq(reportTypeId), any(Pageable.class)))
                .willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query/filter")
                        .param("status", status.name())
                        .param("reportTypeId", reportTypeId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].reportId").value(report1.getReportId()))
                .andExpect(jsonPath("$.data.content[0].status").value(status.name()))
                .andExpect(jsonPath("$.data.content[0].reportType.reportTypeId").value(reportTypeId))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @DisplayName("상태로만 필터링된 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByStatusOnly() throws Exception {
        ReportStatus status = ReportStatus.SUBMITTED;
        given(reportQueryService.findReports(eq(status), any(Pageable.class))).willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query/filter")
                        .param("status", status.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].status").value(status.name()))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @DisplayName("신고 유형으로만 필터링된 신고 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getReportsByTypeOnly() throws Exception {
        Long reportTypeId = 1L;
        given(reportQueryService.findReportsByReportTypeId(eq(reportTypeId), any(Pageable.class))).willReturn(singleReportPage);

        mockMvc.perform(get("/reports/query/filter")
                        .param("reportTypeId", reportTypeId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].reportType.reportTypeId").value(reportTypeId))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("신고된 게시글 목록 조회 API - 성공")
    @WithMockUser
    void getReportedPosts_Success() throws Exception {
        ReportedPostResponse post1 = ReportedPostResponse.builder()
            .postId(1L).postTitle("Reported Post 1").reportCount(5L).lastReportContent("Bad post")
            .build();
        ReportedPostResponse post2 = ReportedPostResponse.builder()
            .postId(2L).postTitle("Reported Post 2").reportCount(3L).lastReportContent("Spam post")
            .build();
        List<ReportedPostResponse> mockResponse = List.of(post1, post2);
        
        when(reportQueryService.findReportedPosts()).thenReturn(mockResponse);

        mockMvc.perform(get("/reports/query/reported-posts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].postId").value(1L))
                .andExpect(jsonPath("$.data[0].postTitle").value("Reported Post 1"))
                .andExpect(jsonPath("$.data[0].reportCount").value(5L))
                .andExpect(jsonPath("$.data[1].postId").value(2L));
    }
    
    @Test
    @DisplayName("신고된 게시글 목록 조회 API - 결과 없음")
    @WithMockUser
    void getReportedPosts_NoResults() throws Exception {
        when(reportQueryService.findReportedPosts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/query/reported-posts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("신고된 댓글 목록 조회 API - 성공")
    @WithMockUser
    void getReportedComments_Success() throws Exception {
        ReportedCommentResponse comment1 = ReportedCommentResponse.builder()
            .commentId(10L).commentContent("Reported Comment 10").reportCount(7L).lastReportContent("Bad comment")
            .build();
        ReportedCommentResponse comment2 = ReportedCommentResponse.builder()
            .commentId(11L).commentContent("Reported Comment 11").reportCount(2L).lastReportContent("Spam comment")
            .build();
        List<ReportedCommentResponse> mockResponse = List.of(comment1, comment2);

        when(reportQueryService.findReportedComments()).thenReturn(mockResponse);

        mockMvc.perform(get("/reports/query/reported-comments")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].commentId").value(10L))
                .andExpect(jsonPath("$.data[0].commentContent").value("Reported Comment 10"))
                .andExpect(jsonPath("$.data[0].reportCount").value(7L))
                .andExpect(jsonPath("$.data[1].commentId").value(11L));
    }
    
    @Test
    @DisplayName("신고된 댓글 목록 조회 API - 결과 없음")
    @WithMockUser
    void getReportedComments_NoResults() throws Exception {
        when(reportQueryService.findReportedComments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/query/reported-comments")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }
} 