package com.newbit.newbitfeatureservice.common.exception;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> response
                = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        StringBuilder errorMessage = new StringBuilder(errorCode.getMessage());
        for(FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(String.format("[%s : %s]", error.getField(), error.getDefaultMessage()));
        }
        ApiResponse<Void> response
                = ApiResponse.failure(errorCode.getCode(), errorMessage.toString());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (MediaType.TEXT_EVENT_STREAM_VALUE.equals(accept)) {
            // SSE 연결에서는 ApiResponse로 감싸지 않고 500만 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SSE Error");
        }

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }
}
