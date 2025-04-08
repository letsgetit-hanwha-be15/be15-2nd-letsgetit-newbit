package com.newbit.post.service;

import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 저장 - 성공")
    void savePost_success() {
        // given
        Post post = new Post();
        post.setTitle("테스트 제목");
        post.setContent("테스트 내용");

        // when
        postService.savePost(post);

        // then
        verify(postRepository, times(1)).save(post);
    }
}
