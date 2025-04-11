package com.newbit.post.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.dto.response.PostDetailResponse;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.newbit.post.entity.Comment;


import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        post.update(request.getTitle(), request.getContent());
        return new PostResponse(post);
    }

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(request.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .likeCount(0)
                .reportCount(0)
                .build();

        postRepository.save(post);
        return new PostResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> searchPosts(String keyword) {
        List<Post> posts = postRepository.searchByKeyword(keyword);
        return posts.stream().map(PostResponse::new).toList();
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        post.softDelete();
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostList(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(PostResponse::new);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        List<Comment> comments = commentRepository.findByPostIdAndDeletedAtIsNull(postId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::new)
                .toList();

        String writerName = post.getUser().getUserName();
        String categoryName = post.getPostCategory().getName();

        return new PostDetailResponse(post, commentResponses, writerName, categoryName);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findByUserIdAndDeletedAtIsNull(userId);
        return posts.stream()
                .map(PostResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPopularPosts() {
        List<Post> posts = postRepository.findPopularPosts(10); // 좋아요 10개 이상
        return posts.stream()
                .map(PostResponse::new)
                .toList();
    }

    @Transactional
    public PostResponse createNotice(PostCreateRequest request, CustomUser user) {
        // 🔐 관리자 권한 체크
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!isAdmin) {
            throw new SecurityException("공지사항은 관리자만 등록할 수 있습니다.");
        }

        // 📝 게시글 생성
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(user.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .likeCount(0)
                .reportCount(0)
                .isNotice(true)
                .build();

        postRepository.save(post);
        return new PostResponse(post);
    }

    @Transactional
    public PostResponse updateNotice(Long postId, PostUpdateRequest request, CustomUser user) {
        // 관리자 권한 체크
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!isAdmin) {
            throw new SecurityException("공지사항은 관리자만 수정할 수 있습니다.");
        }

        // 게시글 조회
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 공지사항 여부 확인
        if (!post.isNotice()) {
            throw new IllegalArgumentException("해당 게시글은 공지사항이 아닙니다.");
        }

        // 수정
        post.update(request.getTitle(), request.getContent());

        return new PostResponse(post);
    }

    @Transactional
    public void deleteNotice(Long postId, CustomUser user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!isAdmin) {
            throw new SecurityException("공지사항은 관리자만 삭제할 수 있습니다.");
        }

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!post.isNotice()) {
            throw new IllegalArgumentException("해당 게시글은 공지사항이 아닙니다.");
        }

        post.softDelete();
    }

}
