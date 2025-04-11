package com.newbit.user.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.user.dto.request.FindIdDTO;
import com.newbit.user.dto.request.FindPasswordDTO;
import com.newbit.user.dto.request.UserRequestDTO;
import com.newbit.user.dto.response.UserIdDTO;
import com.newbit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 관련 API (회원가입, 아이디/비밀번호 찾기 등)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입", description = "회원 가입 기능")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody UserRequestDTO request) {
        userService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));

    }

    @Operation(summary = "아이디 찾기", description = "아이디 찾기 기능")
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<UserIdDTO>> findEmail(@RequestBody FindIdDTO findIdDTO) {
        UserIdDTO userIdDTO = userService.findEmailByNameAndPhone(findIdDTO);
        return ResponseEntity.ok(ApiResponse.success(userIdDTO));
    }

    @Operation(summary = "비밀번호 찾기", description = "비밀번호 찾기 기능")
    @PostMapping("/find-password")
    public ResponseEntity<ApiResponse<Void>> findPassword(@RequestBody FindPasswordDTO requestDto) {
        userService.findPasswordByEmail(requestDto.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
