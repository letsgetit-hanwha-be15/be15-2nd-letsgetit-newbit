package com.newbit.post.service;

import com.newbit.post.dto.request.CommentCreateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        postRepository = mock(PostRepository.class); // ← 추가

        commentService = new CommentService(commentRepository, postRepository); // ← 수정
    }

    @Test
    void 댓글_등록_성공() {
        // given
        Long postId = 10L;

        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글 내용입니다.")
                .userId(1L)
                .postId(postId)
                .build();

        Post mockPost = Post.builder()
                .id(postId)
                .title("제목")
                .content("내용")
                .userId(1L)
                .postCategoryId(2L)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CommentResponse response = commentService.createComment(postId, request);

        // then
        assertThat(response.getContent()).isEqualTo("댓글 내용입니다.");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}
