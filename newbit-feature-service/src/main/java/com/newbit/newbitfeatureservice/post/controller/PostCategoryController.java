package com.newbit.newbitfeatureservice.post.controller;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.dto.response.PostCategoryResponse;
import com.newbit.newbitfeatureservice.post.entity.PostCategory;
import com.newbit.newbitfeatureservice.post.repository.PostCategoryRepository;
import com.newbit.newbitfeatureservice.post.service.PostCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "게시글 카테고리 API", description = "게시글 카테고리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/categories")
public class PostCategoryController {

    private final PostCategoryService postCategoryService;

    @GetMapping
    @Operation(summary = "게시글 카테고리 목록 조회", description = "사이드바 및 필터링을 위한 게시글 카테고리 이름 전체 목록을 조회합니다.")
    public ResponseEntity<List<PostCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(postCategoryService.getAllCategories());
    }
}