package com.newbit.post.service;

import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private PostService postService;
    private PostCreateRequest request;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postService = new PostService(postRepository);

        request = new PostCreateRequest();
        request.setTitle("단위 테스트 제목");
        request.setContent("단위 테스트 내용");
        request.setUserId(1L);
        request.setPostCategoryId(2L);
    }

    @Test
    void 게시글_수정_성공() {
        // given
        Long postId = 1L;
        Post originalPost = Post.builder()
                .id(postId)
                .title("기존 제목")
                .content("기존 내용")
                .userId(1L)
                .postCategoryId(1L)
                .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(originalPost));

        // when
        postService.updatePost(postId, updateRequest);

        // then
        assertThat(originalPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(originalPost.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    void 게시글_수정_실패_게시글이_없음() {
        // given
        Long postId = 999L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.updatePost(postId, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 게시글이 존재하지 않습니다.");
    }

    @Test
    void 게시글_등록_성공() {
        // given
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 Post 그대로 반환

        // when
        postService.createPost(request);

        // then
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void 게시글_삭제_성공() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("삭제할 제목")
                .content("삭제할 내용")
                .userId(1L)
                .postCategoryId(1L)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId);

        assertThat(post.getDeletedAt()).isNotNull();
    }

    @Test
    void 게시글_삭제_실패_게시글이_없음() {
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(postId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 게시글이 존재하지 않습니다.");
    }

    @Test
    void 게시글_목록_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());



        Post post1 = Post.builder()
                .id(1L)
                .title("테스트 제목1")
                .content("테스트 내용1")
                .userId(1L)
                .postCategoryId(1L)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("테스트 제목2")
                .content("테스트 내용2")
                .userId(2L)
                .postCategoryId(1L)
                .build();

        Page<Post> postPage = new PageImpl<>(List.of(post1, post2), pageable, 2);

        when(postRepository.findAll(pageable)).thenReturn(postPage);

        // when
        var result = postService.getPostList(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);

        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 제목1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("테스트 제목2");

        verify(postRepository, times(1)).findAll(pageable);
    }
}
