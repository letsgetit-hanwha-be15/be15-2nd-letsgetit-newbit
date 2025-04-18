package com.newbit.newbitfeatureservice.report.command.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.newbitfeatureservice.report.command.application.dto.request.ReportUpdateRequest;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.Report;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportType;
import com.newbit.newbitfeatureservice.report.command.domain.repository.ReportRepository;
import com.newbit.newbitfeatureservice.report.command.domain.repository.ReportTypeRepository;
import com.newbit.newbitfeatureservice.report.command.infrastructure.repository.JpaReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 통합 테스트 Disabled
@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReportProcessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private JpaReportRepository jpaReportRepository;
    
    @Autowired
    private ReportTypeRepository reportTypeRepository;
    
    private ReportType testReportType;
    
    @BeforeEach
    void setUp() {
        testReportType = reportTypeRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("테스트에 필요한 신고 유형이 DB에 없습니다. ID: 1"));
    }
    
    @Test
    @DisplayName("신고 처리 통합 테스트 - 게시글 삭제")
    void processReportIntegrationTest() throws Exception {
        // 1. 테스트를 위한 게시글 생성
        Post testPost = Post.builder()
                .title("테스트 게시글")
                .content("테스트 내용입니다.")
                .userId(1L)
                .postCategoryId(1L)
                .isNotice(false)
                .build();
        Post savedPost = postRepository.save(testPost);
                
        // 2. 신고 생성
        ReportCreateRequest reportCreateRequest = new ReportCreateRequest(1L, savedPost.getId(), testReportType.getId(), "테스트 신고입니다.");
        
        mockMvc.perform(post("/api/v1/reports/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportCreateRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
        
        // 3. 신고 처리 (관리자)
        Report report = jpaReportRepository.findAllByPostId(savedPost.getId()).get(0);
        ReportUpdateRequest reportUpdateRequest = new ReportUpdateRequest(report.getReportId(), ReportStatus.DELETED);
        
        mockMvc.perform(patch("/api/v1/admin/reports/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
        
        // 4. 결과 검증 - 게시글이 삭제되었는지 확인
        Post updatedPost = postRepository.findById(savedPost.getId()).orElse(null);
        assertNotNull(updatedPost);
        assertNotNull(updatedPost.getDeletedAt());
        
        // 5. 신고 상태 확인
        Report updatedReport = reportRepository.findById(report.getReportId());
        assertEquals(ReportStatus.DELETED, updatedReport.getStatus());
    }
    
    @Test
    @DisplayName("신고 임계값(50) 도달 시 게시글 자동 삭제 테스트")
    void reportThresholdIntegrationTest() throws Exception {
        // 1. 테스트를 위한 게시글 생성
        Post testPost = Post.builder()
                .title("신고 임계값 테스트 게시글")
                .content("이 게시글은 신고가 많이 들어옵니다.")
                .userId(1L)
                .postCategoryId(1L)
                .isNotice(false)
                .reportCount(49)
                .build();
        Post savedPost = postRepository.save(testPost);
        
        // 2. 신고 생성
        ReportCreateRequest reportCreateRequest = new ReportCreateRequest(1L, savedPost.getId(), testReportType.getId(), "임계값 테스트 신고입니다.");
        
        mockMvc.perform(post("/api/v1/reports/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportCreateRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
        
        // 3. 결과 검증 - 게시글이 자동으로 삭제되었는지 확인
        Post updatedPost = postRepository.findById(savedPost.getId()).orElse(null);
        assertNotNull(updatedPost);
        assertEquals(50, updatedPost.getReportCount());
        assertNotNull(updatedPost.getDeletedAt());
    }
} 