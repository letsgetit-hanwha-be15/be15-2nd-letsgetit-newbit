package com.newbit.column.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.request.UpdateColumnRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@DisplayName("멘토 칼럼 등록 요청 성공")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ColumnRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createColumnRequest_success() throws Exception {
        // given
        CreateColumnRequestDto requestDto = CreateColumnRequestDto.builder()
                .title("테스트 칼럼 제목")
                .content("이것은 테스트 칼럼 내용입니다.")
                .price(1000)
                .thumbnailUrl("https://example.com/test-thumbnail.jpg")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/columns/requests")
                        .param("mentorId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.columnRequestId").exists())
                .andDo(print());
    }

    @DisplayName("멘토 칼럼 수정 요청 성공")
    @Test
    void updateColumnRequest_success() throws Exception {
        // given
        Long columnId = 1L;
        UpdateColumnRequestDto requestDto = UpdateColumnRequestDto.builder()
                .title("수정된 칼럼 제목")
                .content("이것은 수정된 칼럼 내용입니다.")
                .price(2000)
                .thumbnailUrl("https://example.com/updated-thumbnail.jpg")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/columns/requests/{columnId}/edit", columnId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.columnRequestId").exists())
                .andDo(print());
    }

}
