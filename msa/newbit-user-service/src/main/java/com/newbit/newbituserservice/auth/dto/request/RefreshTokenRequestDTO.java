package com.newbit.newbituserservice.auth.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshTokenRequestDTO {
    private final String refreshToken;
}
