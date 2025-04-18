package com.newbit.newbituserservice.auth.controller;

import com.newbit.newbituserservice.auth.dto.request.LoginRequestDTO;
import com.newbit.newbituserservice.auth.dto.request.RefreshTokenRequestDTO;
import com.newbit.newbituserservice.auth.dto.response.TokenResponseDTO;
import com.newbit.newbituserservice.auth.service.AuthService;
import com.newbit.newbituserservice.common.dto.ApiResponse;
import com.newbit.newbituserservice.user.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API", description = "인증 관련 API (토큰 발급, 로그인, 로그아웃 등)")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginService loginService;

    @Operation(summary = "로그인", description = "로그인 기능")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        TokenResponseDTO token = authService.login(request);
        loginService.handleLoginSuccess(token.getUserId());
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @Operation(summary = "refresh 토큰 재발급", description = "refresh 토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> refreshToken(
            @RequestBody RefreshTokenRequestDTO request
    ){
        TokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "로그아웃", description = "로그아웃 기능")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
