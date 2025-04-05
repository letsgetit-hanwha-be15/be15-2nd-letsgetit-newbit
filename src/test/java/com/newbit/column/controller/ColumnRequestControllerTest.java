package com.newbit.column.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.entity.ColumnRequest;
import com.newbit.column.repository.ColumnRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ColumnRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ColumnRequestRepository columnRequestRepository;

    @Test
    @DisplayName("멘토 칼럼 등록 요청을 성공적으로 생성한다")
    void createColumnRequest_success() throws Exception {
        // given
        CreateColumnRequestDto dto = CreateColumnRequestDto.builder()
                .title("칼럼 제목")
                .content("칼럼 내용")
                .price(1000)
                .thumbnailUrl("http://image.url")
                .build();

        Long mentorId = 1L;

        // when
        mockMvc.perform(post("/api/columns/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("mentorId", mentorId.toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // then
        assertThat(columnRequestRepository.findAll())
                .anyMatch(cr -> cr.getUpdatedTitle().equals("JPA 고급 매핑 정리"));
    }
}
