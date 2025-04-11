package com.newbit.post.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import com.newbit.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceNoticeTest {

    private PostRepository postRepository;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postService = new PostService(postRepository, null); // 댓글은 필요 없음
    }

    @Test
    void 공지사항_등록_성공_관리자_권한() {
        // given
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("공지사항입니다");
        request.setContent("중요한 공지입니다.");
        request.setPostCategoryId(1L);

        CustomUser adminUser = CustomUser.builder()
                .userId(1L)
                .email("admin@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_ADMIN"))
                .build();

        // Post 저장 시 반환될 객체 설정
        Post mockPost = Post.builder()
                .id(1L)
                .title(request.getTitle())
                .content(request.getContent())
                .userId(adminUser.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .isNotice(true)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        // when
        PostResponse result = postService.createNotice(request, adminUser);

        // then
        assertThat(result.getTitle()).isEqualTo("공지사항입니다");
        assertThat(result.isNotice()).isTrue();
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void 공지사항_등록_실패_권한없음() {
        // given
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("공지사항입니다");
        request.setContent("중요한 공지입니다.");
        request.setPostCategoryId(1L);

        CustomUser normalUser = CustomUser.builder()
                .userId(2L)
                .email("user@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_USER"))
                .build();

        // when & then
        assertThatThrownBy(() -> postService.createNotice(request, normalUser))
                .isInstanceOf(SecurityException.class)
                .hasMessage("공지사항은 관리자만 등록할 수 있습니다.");

        verify(postRepository, never()).save(any(Post.class));
    }


    @Test
    void 공지사항_수정_성공() {
        // given
        Long postId = 1L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 공지 제목")
                .content("수정된 공지 내용")
                .build();

        Post originalPost = Post.builder()
                .id(postId)
                .title("기존 제목")
                .content("기존 내용")
                .userId(1L)
                .postCategoryId(1L)
                .isNotice(true)
                .build();

        CustomUser adminUser = CustomUser.builder()
                .userId(1L)
                .email("admin@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_ADMIN"))
                .build();

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(originalPost));

        // when
        PostResponse response = postService.updateNotice(postId, updateRequest, adminUser);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 공지 제목");
        assertThat(response.getContent()).isEqualTo("수정된 공지 내용");
    }

    @Test
    void 공지사항_수정_실패_권한없음() {
        // given
        Long postId = 1L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정 제목")
                .content("수정 내용")
                .build();

        CustomUser normalUser = CustomUser.builder()
                .userId(2L)
                .email("user@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_USER"))
                .build();

        // when & then
        assertThatThrownBy(() -> postService.updateNotice(postId, updateRequest, normalUser))
                .isInstanceOf(SecurityException.class)
                .hasMessage("공지사항은 관리자만 수정할 수 있습니다.");

        verify(postRepository, never()).findById(any());
    }

    @Test
    void 공지사항_수정_실패_공지사항_아님() {
        // given
        Long postId = 1L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정 제목")
                .content("수정 내용")
                .build();

        Post nonNoticePost = Post.builder()
                .id(postId)
                .title("일반글")
                .content("내용")
                .userId(1L)
                .postCategoryId(1L)
                .isNotice(false)
                .build();

        CustomUser adminUser = CustomUser.builder()
                .userId(1L)
                .email("admin@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_ADMIN"))
                .build();

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(nonNoticePost));

        // when & then
        assertThatThrownBy(() -> postService.updateNotice(postId, updateRequest, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 게시글은 공지사항이 아닙니다.");
    }

    @Test
    void 공지사항_수정_실패_게시글_없음() {
        // given
        Long postId = 99L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정 제목")
                .content("수정 내용")
                .build();

        CustomUser adminUser = CustomUser.builder()
                .userId(1L)
                .email("admin@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_ADMIN"))
                .build();

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.updateNotice(postId, updateRequest, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 게시글이 존재하지 않습니다.");
    }

}
