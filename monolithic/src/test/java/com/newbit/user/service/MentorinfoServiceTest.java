package com.newbit.user.service;

import com.newbit.user.dto.response.ColumnDTO;
import com.newbit.user.dto.response.MentorProfileDTO;
import com.newbit.user.dto.response.PostDTO;
import com.newbit.user.dto.response.SeriesDTO;
import com.newbit.user.mapper.UserMapper;
import com.newbit.user.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MentorProfileServiceTest {

    @InjectMocks
    private UserQueryService userQueryService;

    @Mock
    private UserMapper userMapper;

    @Test
    void getMentorProfile_success() {
        Long mentorId = 1L;

        // MentorProfileDTO 세팅
        MentorProfileDTO profile = new MentorProfileDTO();
        profile.setUserName("홍길동");
        profile.setNickname("길동이");
        profile.setProfileImageUrl("image.jpg");
        profile.setJobName("백엔드 개발자");
        profile.setTemperature(85);
        profile.setPreferredTime("09:00-12:00");
        profile.setIsActive(true);
        profile.setPrice(1500);
        profile.setTechstackName("Java, Spring");

        // PostDTO 리스트
        PostDTO post = new PostDTO();
        post.setPostId(1L);
        post.setTitle("제목1");
        post.setNickname("작성자1");
        post.setCreatedAt(LocalDateTime.now());
        List<PostDTO> posts = List.of(post);

        // ColumnDTO 리스트
        ColumnDTO column = new ColumnDTO();
        column.setColumnId(1L);
        column.setTitle("컬럼1");
        column.setNickname("작성자1");
        column.setCreatedAt(LocalDateTime.now());
        List<ColumnDTO> columns = List.of(column);

        // SeriesDTO 리스트
        SeriesDTO series = new SeriesDTO();
        series.setSeriesId(1L);
        series.setTitle("시리즈1");
        series.setNickname("작성자1");
        series.setCreatedAt(LocalDateTime.now());
        List<SeriesDTO> seriesList = List.of(series);

        // Stubbing
        given(userMapper.findMentorProfile(mentorId)).willReturn(profile);
        given(userMapper.findMentorPosts(mentorId)).willReturn(posts);
        given(userMapper.findMentorColumns(mentorId)).willReturn(columns);
        given(userMapper.findMentorSeries(mentorId)).willReturn(seriesList);

        // when
        MentorProfileDTO result = userQueryService.getMentorProfile(mentorId);

        // then
        assertThat(result.getNickname()).isEqualTo("길동이");
        assertThat(result.getPosts()).hasSize(1);
        assertThat(result.getColumns()).hasSize(1);
        assertThat(result.getSeries()).hasSize(1);
    }
}
