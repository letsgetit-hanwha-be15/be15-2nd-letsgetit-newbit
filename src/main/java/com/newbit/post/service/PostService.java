package com.newbit.post.service;

import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

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
}
