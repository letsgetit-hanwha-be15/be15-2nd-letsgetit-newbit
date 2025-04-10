package com.newbit.post.service;

import com.newbit.post.dto.request.CommentCreateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
        postRepository = mock(PostRepository.class);

        commentService = new CommentService(commentRepository, postRepository);
    }

    @Test
    void 댓글_조회_성공() {
        // given
        Long postId = 1L;

        Post post = Post.builder()
                .id(postId)
                .title("테스트 게시글")
                .content("내용")
                .userId(1L)
                .postCategoryId(2L)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .content("첫 번째 댓글")
                .userId(1L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .content("두 번째 댓글")
                .userId(2L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        List<Comment> mockComments = Arrays.asList(comment1, comment2);

        when(commentRepository.findByPostIdAndDeletedAtIsNull(postId)).thenReturn(mockComments);

        // when
        List<CommentResponse> responses = commentService.getCommentsByPostId(postId);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getContent()).isEqualTo("첫 번째 댓글");
        assertThat(responses.get(1).getContent()).isEqualTo("두 번째 댓글");
        verify(commentRepository, times(1)).findByPostIdAndDeletedAtIsNull(postId);
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
