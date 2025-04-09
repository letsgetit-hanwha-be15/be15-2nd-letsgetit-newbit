package com.newbit.post.service;

import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.request.PostCreateRequest;

import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postService = new PostService(postRepository);
    }

    @Test
    void 게시글_수정_성공() {
        // given
        Long postId = 1L;
        Post originalPost = Post.builder()
                .id(postId)
                .title("기존 제목")
                .content("기존 내용")
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


    private PostCreateRequest request;

    @BeforeEach
    void setUp() {
        request = new PostCreateRequest();
        request.setTitle("단위 테스트 제목");
        request.setContent("단위 테스트 내용");
        request.setUserId(1L);
        request.setPostCategoryId(2L);
    }

    @Test
    void 게시글_등록_성공() {
        // given
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 Post를 그대로 리턴한다고 가정

        // when
        postService.createPost(request);

        // then
        verify(postRepository, times(1)).save(any(Post.class)); // postRepository.save()가 한 번 호출됐는지 확인

    }
}