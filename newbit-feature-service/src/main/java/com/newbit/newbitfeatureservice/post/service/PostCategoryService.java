package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.entity.PostCategory;
import com.newbit.newbitfeatureservice.post.repository.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    public String getCategoryNameById(Long id) {
        PostCategory category = postCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_CATEGORY_NOT_FOUND));
        return category.getName();
    }
}