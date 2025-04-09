package com.newbit.post.service;

import com.newbit.post.dto.PostCreateRequest;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private PostCreateRequest request;

    @BeforeEach
    void setUp() {
        request = new PostCreateRequest();
        request.setTitle("단위 테스트 제목");
        request.setContent("단위 테스트 내용");
        request.setUserId(1L);
        request.setPostCategoryId(2L);
    }

    @Test
    void 게시글_등록_성공() {
        // given
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 Post를 그대로 리턴한다고 가정

        // when
        postService.createPost(request);

        // then
        verify(postRepository, times(1)).save(any(Post.class)); // postRepository.save()가 한 번 호출됐는지 확인
    }
}
