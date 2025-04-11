package com.newbit.post.service;

import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.dto.response.PostDetailResponse;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.purchase.command.application.service.PointTransactionCommandService;
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
    private final PointTransactionCommandService pointTransactionCommandService;

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
        pointTransactionCommandService.givePointByType(request.getUserId(), "게시글 적립", post.getId());
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
}
