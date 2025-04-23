package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.dto.request.PostCreateRequest;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostInternalService {

    private final PostRepository postRepository;

    @Transactional
    public Post createPostInternal(PostCreateRequest request, CustomUser user) {
        if (user == null || user.getAuthorities().stream()
                .noneMatch(auth -> "ROLE_USER".equals(auth.getAuthority()))) {
            throw new BusinessException(ErrorCode.ONLY_USER_CAN_CREATE_POST);
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(user.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .likeCount(0)
                .reportCount(0)
                .isNotice(false)
                .build();

        return postRepository.save(post);
    }
}