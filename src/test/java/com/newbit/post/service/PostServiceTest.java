package com.newbit.post.service;

import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Old Title");
        post.setContent("Old Content");
    }

    @Test
    void updatePost_success() {
        // given
        Long postId = 1L;
        String newTitle = "New Title";
        String newContent = "New Content";

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        postService.post(postId, newTitle, newContent);

        // then
        assertThat(post.getTitle()).isEqualTo(newTitle);
        assertThat(post.getContent()).isEqualTo(newContent);
    }

    @Test
    void updatePost_fail_postNotFound() {
        // given
        Long invalidId = 999L;
        when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.post(invalidId, "x", "y"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 게시글이 없습니다.");
    }
}
