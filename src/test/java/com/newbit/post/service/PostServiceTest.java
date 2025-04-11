package com.newbit.post.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.entity.PostCategory;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private PostService postService;
    private PostCreateRequest request;
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        commentRepository = mock(CommentRepository.class);
        postService = new PostService(postRepository, commentRepository);

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
    void 게시글_등록_성공_일반사용자() {
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
    void 게시글_등록_실패_관리자() {
        // given
        CustomUser adminUser = CustomUser.builder()
                .userId(2L)
                .email("admin@example.com")
                .password("encoded")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        PostCreateRequest adminRequest = new PostCreateRequest();
        adminRequest.setTitle("관리자 글");
        adminRequest.setContent("관리자가 작성한 글");
        adminRequest.setPostCategoryId(1L);

        // when & then
        assertThatThrownBy(() -> postService.createPost(adminRequest, adminUser))
                .isInstanceOf(SecurityException.class)
                .hasMessage("게시글은 일반 사용자만 작성할 수 있습니다.");

        verify(postRepository, never()).save(any(Post.class));
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

        User mockUser = User.builder()
                .userId(1L)
                .userName("작성자이름")
                .build();

        PostCategory mockCategory = PostCategory.builder()
                .id(2L)
                .name("카테고리이름")
                .build();

        post.setUser(mockUser);
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
        PostService postServiceWithMocks = new PostService(postRepository, commentRepository);

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
}
