package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.dto.request.PostCreateRequest;
import com.newbit.newbitfeatureservice.post.entity.Attachment;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.entity.PostCategory;
import com.newbit.newbitfeatureservice.post.repository.AttachmentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostCategoryRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostInternalService {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public Post createPostInternal(PostCreateRequest request, CustomUser user) {
        // 🔍 PostCategory 엔티티 조회
        PostCategory postCategory = postCategoryRepository.findById(request.getPostCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_CATEGORY_NOT_FOUND));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(user.getUserId())
                .postCategoryId(postCategory.getId())
                .postCategory(postCategory)
                .likeCount(0)
                .reportCount(0)
                .isNotice(false)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                Attachment attachment = Attachment.builder()
                        .post(post)
                        .imageUrl(imageUrl)
                        .build();
                attachmentRepository.save(attachment);
            }
        }

        return postRepository.save(post);
    }
}
