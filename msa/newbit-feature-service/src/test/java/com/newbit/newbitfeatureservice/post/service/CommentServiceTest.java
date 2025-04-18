package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.post.dto.request.CommentCreateRequest;
import com.newbit.newbitfeatureservice.post.dto.response.CommentResponse;
import com.newbit.newbitfeatureservice.post.entity.Comment;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.repository.CommentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        postRepository = mock(PostRepository.class);
        PointTransactionCommandService pointTransactionCommandService = mock(PointTransactionCommandService.class);
        NotificationCommandService notificationCommandService = mock(NotificationCommandService.class);

        commentService = new CommentService(commentRepository, postRepository, pointTransactionCommandService, notificationCommandService);
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
                .build();

        CustomUser user = CustomUser.builder()
                .userId(1L)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
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

        CommentResponse response = commentService.createComment(postId, request, user);

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

        CustomUser user = CustomUser.builder()
                .userId(1L) // 작성자 본인
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(postId, commentId, user);

        assertThat(comment.getDeletedAt()).isNotNull();
        verify(commentRepository).findById(commentId);
    }

    @Test
    void 댓글_삭제_실패_댓글이_존재하지_않음() {
        Long postId = 1L;
        Long commentId = 999L;

        CustomUser user = CustomUser.builder()
                .userId(1L)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(postId, commentId, user))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
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

        CustomUser user = CustomUser.builder()
                .userId(1L)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(wrongPostId, commentId, user))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_POST_MISMATCH);
    }

    @Test
    void 댓글_삭제_실패_작성자가_아님() {
        Long postId = 1L;
        Long commentId = 100L;

        Post post = Post.builder().id(postId).build();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("삭제 대상 댓글")
                .userId(999L) // 작성자 아님
                .post(post)
                .build();

        CustomUser user = CustomUser.builder()
                .userId(1L) // 삭제 시도자
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(postId, commentId, user))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED_TO_DELETE_COMMENT);
    }

    @Test
    void increaseReportCount_정상처리() {
        // given
        Long commentId = 1L;
        Comment comment = Comment.builder()
                .reportCount(2)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.increaseReportCount(commentId);

        // then
        assertThat(comment.getReportCount()).isEqualTo(3);
    }


    @Test
    void increaseReportCount_댓글없음_예외() {
        // given
        Long commentId = 999L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.increaseReportCount(commentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    void getReportCount_정상처리() {
        // given
        Long commentId = 1L;
        Comment comment = Comment.builder()
                .id(commentId)
                .reportCount(5)
                .post(Post.builder().id(1L).build())
                .userId(1L)
                .content("댓글 내용")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        int result = commentService.getReportCount(commentId);

        // then
        assertThat(result).isEqualTo(5);
    }


    @Test
    void getReportCount_댓글없음_예외() {
        // given
        Long commentId = 888L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.getReportCount(commentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.COMMENT_NOT_FOUND.getMessage());
    }
}
