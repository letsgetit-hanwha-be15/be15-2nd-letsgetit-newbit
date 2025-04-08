package com.newbit.post.service;

import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // 글 저장 처리
    public void savePost(Post post) {
        postRepository.save(post);
    }
}
