package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.dto.request.CommentCreateRequest;
import com.newbit.newbitfeatureservice.post.entity.Comment;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.repository.CommentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentInternalService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    @Transactional
    protected Comment saveCommentInternal(Long postId, CommentCreateRequest request, CustomUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(user.getUserId())
                .reportCount(0)
                .post(post)
                .build();

        commentRepository.save(comment);
        return comment;
    }

}
