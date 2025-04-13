package com.newbit.payment.controller;

import org.springframework.http.ResponseEntity;

import com.newbit.common.dto.ApiResponse;

public abstract class AbstractApiController {

    protected <T> ResponseEntity<ApiResponse<T>> successResponse(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    protected ResponseEntity<ApiResponse<String>> successMessage(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }
} 