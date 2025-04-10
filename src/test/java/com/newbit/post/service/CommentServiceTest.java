package com.newbit.post.service;

import com.newbit.post.dto.request.CommentCreateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.purchase.command.application.service.PointTransactionCommandService;
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
    private PointTransactionCommandService pointTransactionCommandService;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        postRepository = mock(PostRepository.class);
        pointTransactionCommandService = mock(PointTransactionCommandService.class);

        commentService = new CommentService(commentRepository, postRepository, pointTransactionCommandService);
    }

    @Test
    void 댓글_조회_성공() {
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

        when(commentRepository.findByPostIdAndDeletedAtIsNull(postId))
                .thenReturn(Arrays.asList(comment1, comment2));

        List<CommentResponse> responses = commentService.getCommentsByPostId(postId);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getContent()).isEqualTo("첫 번째 댓글");
        assertThat(responses.get(1).getContent()).isEqualTo("두 번째 댓글");
    }

    @Test
    void 댓글_등록_성공() {
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

        CommentResponse response = commentService.createComment(postId, request);

        assertThat(response.getContent()).isEqualTo("댓글 내용입니다.");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void 댓글_삭제_성공() {
        Long postId = 1L;
        Long commentId = 100L;

        Post post = Post.builder().id(postId).build();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("삭제할 댓글입니다.")
                .userId(1L)
                .post(post)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(postId, commentId);

        assertThat(comment.getDeletedAt()).isNotNull();
        verify(commentRepository).findById(commentId);
    }

    @Test
    void 댓글_삭제_실패_댓글이_존재하지_않음() {
        Long postId = 1L;
        Long commentId = 999L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(postId, commentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 댓글이 존재하지 않습니다.");
    }

    @Test
    void 댓글_삭제_실패_postId_불일치() {
        Long postId = 1L;
        Long wrongPostId = 2L;
        Long commentId = 100L;

        Post post = Post.builder().id(postId).build();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("댓글 내용")
                .userId(1L)
                .post(post)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(wrongPostId, commentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다.");
    }
}
