package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.dto.response.PostCategoryResponse;
import com.newbit.newbitfeatureservice.post.entity.PostCategory;
import com.newbit.newbitfeatureservice.post.repository.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    public String getCategoryNameById(Long id) {
        PostCategory category = postCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_CATEGORY_NOT_FOUND));
        return category.getName();
    }

    public List<PostCategoryResponse> getAllCategories() {
        List<PostCategory> categories = postCategoryRepository.findAll();
        return categories.stream()
                .map(category -> new PostCategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
}