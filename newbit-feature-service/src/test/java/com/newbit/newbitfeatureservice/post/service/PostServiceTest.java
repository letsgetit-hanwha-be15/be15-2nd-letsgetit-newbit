package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.dto.request.PostCreateRequest;
import com.newbit.newbitfeatureservice.post.dto.request.PostUpdateRequest;
import com.newbit.newbitfeatureservice.post.dto.response.PostResponse;
import com.newbit.newbitfeatureservice.post.entity.Comment;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.entity.PostCategory;
import com.newbit.newbitfeatureservice.post.repository.CommentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private PostService postService;
    private PointTransactionCommandService pointTransactionCommandService;
    private PostCreateRequest request;
    private CommentRepository commentRepository;
    private UserFeignClient userFeignClient;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        commentRepository = mock(CommentRepository.class);
        pointTransactionCommandService = mock(PointTransactionCommandService.class);
        userFeignClient = mock(UserFeignClient.class);

        postService = new PostService(postRepository, commentRepository, pointTransactionCommandService, userFeignClient);

        request = new PostCreateRequest();
        request.setTitle("단위 테스트 제목");
        request.setContent("단위 테스트 내용");
        request.setPostCategoryId(2L);
    }

    @Test
    void 게시글_등록_성공_회원() {
        // given
        CustomUser user = CustomUser.builder()
                .userId(1L)
                .email("user@example.com")
                .password("encoded")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        PostCreateRequest userRequest = new PostCreateRequest();
        userRequest.setTitle("일반 글");
        userRequest.setContent("내용");
        userRequest.setPostCategoryId(1L);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PostResponse response = postService.createPost(userRequest, user);

        // then
        assertThat(response.getTitle()).isEqualTo("일반 글");
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void 게시글_등록_실패_비회원() {
        // given
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("비회원 테스트");
        request.setContent("비회원은 작성 못함");
        request.setPostCategoryId(1L);

        CustomUser unauthenticatedUser = null;

        // when & then
        assertThatThrownBy(() -> postService.createPost(request, unauthenticatedUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ONLY_USER_CAN_CREATE_POST.getMessage());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void 게시글_목록_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());



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

    @Test
    void 게시글_상세_조회_성공() {
        // given
        Long postId = 1L;

        Post post = Post.builder()
                .id(postId)
                .title("상세 제목")
                .content("상세 내용")
                .userId(1L)
                .postCategoryId(2L)
                .build();

        PostCategory mockCategory = PostCategory.builder()
                .id(2L)
                .name("카테고리이름")
                .build();

        post.setPostCategory(mockCategory);

        Comment comment = Comment.builder()
                .id(1L)
                .post(post)
                .userId(2L)
                .content("댓글입니다")
                .build();

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));
        CommentRepository commentRepository = mock(CommentRepository.class);
        when(commentRepository.findByPostIdAndDeletedAtIsNull(postId)).thenReturn(List.of(comment));

        // PostService를 다시 생성해서 의존성 주입
        PointTransactionCommandService pointTransactionCommandService = mock(PointTransactionCommandService.class);
        PostService postServiceWithMocks = new PostService(postRepository, commentRepository, pointTransactionCommandService, userFeignClient);


        // when
        var response = postServiceWithMocks.getPostDetail(postId);

        // then
        assertThat(response.getTitle()).isEqualTo("상세 제목");
        assertThat(response.getWriterName()).isEqualTo("작성자이름");
        assertThat(response.getCategoryName()).isEqualTo("카테고리이름");
        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getComments().get(0).getContent()).isEqualTo("댓글입니다");
    }

    @Test
    void 본인_게시글_조회_성공() {
        // given
        Long userId = 1L;

        Post post1 = Post.builder()
                .id(1L)
                .title("내 게시글 1")
                .content("내용 1")
                .userId(userId)
                .postCategoryId(1L)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("내 게시글 2")
                .content("내용 2")
                .userId(userId)
                .postCategoryId(1L)
                .build();

        List<Post> myPosts = List.of(post1, post2);

        when(postRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(myPosts);

        // when
        List<PostResponse> result = postService.getMyPosts(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("내 게시글 1");
        assertThat(result.get(1).getTitle()).isEqualTo("내 게시글 2");

        verify(postRepository, times(1)).findByUserIdAndDeletedAtIsNull(userId);
    }

    @Test
    void 인기_게시글_조회_성공() {
        // given
        Post post1 = Post.builder()
                .id(1L)
                .title("인기글 1")
                .content("내용 1")
                .likeCount(15)
                .userId(1L)
                .postCategoryId(1L)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("인기글 2")
                .content("내용 2")
                .likeCount(12)
                .userId(2L)
                .postCategoryId(1L)
                .build();

        List<Post> popularPosts = List.of(post1, post2);

        when(postRepository.findPopularPosts(10)).thenReturn(popularPosts);

        // when
        List<PostResponse> result = postService.getPopularPosts();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("인기글 1");
        assertThat(result.get(1).getTitle()).isEqualTo("인기글 2");

        verify(postRepository, times(1)).findPopularPosts(10);
    }

    @Test
    void 게시글_수정_성공() {
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

        CustomUser user = CustomUser.builder()
                .userId(1L)
                .email("user@example.com")
                .password("encoded")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(originalPost));

        postService.updatePost(postId, updateRequest, user);

        assertThat(originalPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(originalPost.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    void 게시글_수정_실패_게시글이_없음() {
        Long postId = 999L;
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        CustomUser user = CustomUser.builder()
                .userId(1L)
                .email("user@example.com")
                .password("encoded")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.updatePost(postId, updateRequest, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    void 게시글_수정_실패_작성자가_아님() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("원래 제목")
                .content("원래 내용")
                .userId(100L) // 작성자 ID
                .postCategoryId(1L)
                .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정 시도 제목")
                .content("수정 시도 내용")
                .build();

        CustomUser user = CustomUser.builder()
                .userId(1L) // 다른 사용자
                .email("not-author@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(postId, updateRequest, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_TO_UPDATE_POST.getMessage());
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

        CustomUser user = CustomUser.builder()
                .userId(1L) // 작성자 본인
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, user);

        assertThat(post.getDeletedAt()).isNotNull();
    }


    @Test
    void 게시글_삭제_실패_게시글이_없음() {
        Long postId = 999L;

        CustomUser user = CustomUser.builder()
                .userId(1L)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(postId, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    void 게시글_삭제_실패_작성자가_아님() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .title("삭제 대상")
                .content("삭제 내용")
                .userId(100L) // 실제 작성자
                .postCategoryId(1L)
                .build();

        CustomUser user = CustomUser.builder()
                .userId(1L) // 삭제 시도자
                .email("not-author@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(postId, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_TO_DELETE_POST.getMessage());
    }


    @Test
    void increaseLikeCount_정상처리() {
        // given
        Long postId = 1L;
        Post post = mock(Post.class);

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));

        // when
        postService.increaseLikeCount(postId);

        // then
        verify(post, times(1)).increaseLikeCount();
    }

    @Test
    void decreaseLikeCount_정상처리() {
        // given
        Long postId = 2L;
        Post post = mock(Post.class);

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));

        // when
        postService.decreaseLikeCount(postId);

        // then
        verify(post, times(1)).decreaseLikeCount();
    }

    @Test
    void increaseReportCount_정상처리() {
        // given
        Long postId = 3L;
        Post post = mock(Post.class);

        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));

        // when
        postService.increaseReportCount(postId);

        // then
        verify(post, times(1)).increaseReportCount();
    }

    @Test
    void getReportCountByPostId_성공() {
        // given
        Long postId = 4L;
        Post post = mock(Post.class);
        when(post.getReportCount()).thenReturn(5);
        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));

        // when
        int result = postService.getReportCountByPostId(postId);

        // then
        assertThat(result).isEqualTo(5);
    }

    @Test
    void getWriterIdByPostId_성공() {
        // given
        Long postId = 5L;
        Long writerId = 123L;

        when(postRepository.findUserIdByPostId(postId)).thenReturn(Optional.of(writerId));

        // when
        Long result = postService.getWriterIdByPostId(postId);

        // then
        assertThat(result).isEqualTo(writerId);
    }

    @Test
    void getWriterIdByPostId_실패_게시글없음() {
        // given
        Long postId = 99L;
        when(postRepository.findUserIdByPostId(postId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getWriterIdByPostId(postId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.POST_NOT_FOUND.getMessage());
    }
}

