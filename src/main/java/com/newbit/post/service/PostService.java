package com.newbit.post.service;

import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

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
}
