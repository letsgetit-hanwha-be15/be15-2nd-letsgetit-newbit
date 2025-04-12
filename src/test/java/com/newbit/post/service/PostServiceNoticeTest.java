package com.newbit.post.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.exception.BusinessException;
import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.purchase.command.application.service.PointTransactionCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceNoticeTest {

    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private PointTransactionCommandService pointTransactionCommandService;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        commentRepository = mock(CommentRepository.class);
        pointTransactionCommandService = mock(PointTransactionCommandService.class);

        postService = new PostService(postRepository, commentRepository, pointTransactionCommandService);
    }

    @Test
    void 공지사항_등록_성공_관리자_권한() {
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

        Post mockPost = Post.builder()
                .id(1L)
                .title(request.getTitle())
                .content(request.getContent())
                .userId(adminUser.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .isNotice(true)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        PostResponse result = postService.createNotice(request, adminUser);

        assertThat(result.getTitle()).isEqualTo("공지사항입니다");
        assertThat(result.isNotice()).isTrue();
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void 공지사항_등록_실패_권한없음() {
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

        assertThatThrownBy(() -> postService.createNotice(request, normalUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("공지사항은 관리자만 등록할 수 있습니다.");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void 공지사항_수정_성공() {
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

        PostResponse response = postService.updateNotice(postId, updateRequest, adminUser);

        assertThat(response.getTitle()).isEqualTo("수정된 공지 제목");
        assertThat(response.getContent()).isEqualTo("수정된 공지 내용");
    }

    @Test
    void 공지사항_수정_실패_권한없음() {
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

        assertThatThrownBy(() -> postService.updateNotice(postId, updateRequest, normalUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("공지사항은 관리자만 수정할 수 있습니다.");

        verify(postRepository, never()).findById(any());
    }

    @Test
    void 공지사항_수정_실패_공지사항_아님() {
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

        assertThatThrownBy(() -> postService.updateNotice(postId, updateRequest, adminUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("해당 게시글은 공지사항이 아닙니다.");
    }

    @Test
    void 공지사항_수정_실패_게시글_없음() {
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

        assertThatThrownBy(() -> postService.updateNotice(postId, updateRequest, adminUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("해당 게시글이 존재하지 않습니다.");
    }

    @Test
    void 공지사항_삭제_성공() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("공지사항")
                .content("내용")
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

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));

        postService.deleteNotice(postId, adminUser);

        assertThat(post.getDeletedAt()).isNotNull();
    }

    @Test
    void 공지사항_삭제_실패_권한없음() {
        Long postId = 1L;
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("수정 제목")
                .content("수정 내용")
                .build();

        CustomUser normalUser = CustomUser.builder()
                .userId(2L)
                .email("user@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_USER"))
                .build();

        assertThatThrownBy(() -> postService.deleteNotice(postId, normalUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("공지사항은 관리자만 삭제할 수 있습니다.");

        verify(postRepository, never()).findById(any());
    }

    @Test
    void 공지사항_삭제_실패_공지사항_아님() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("일반 글")
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

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deleteNotice(postId, adminUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("해당 게시글은 공지사항이 아닙니다.");
    }

    @Test
    void 공지사항_삭제_실패_게시글_없음() {
        Long postId = 99L;

        CustomUser adminUser = CustomUser.builder()
                .userId(1L)
                .email("admin@example.com")
                .password("encoded")
                .authorities(Collections.singleton(() -> "ROLE_ADMIN"))
                .build();

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deleteNotice(postId, adminUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage("해당 게시글이 존재하지 않습니다.");
    }
}
