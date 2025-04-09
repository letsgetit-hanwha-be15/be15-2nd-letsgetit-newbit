package com.newbit.post.service;

import com.newbit.post.dto.PostCreateRequest;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void createPost(PostCreateRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(request.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .likeCount(0)
                .reportCount(0)
                .build();

        postRepository.save(post);
    }
}
