package com.newbit.newbitfeatureservice.security.filter;

import com.newbit.newbitfeatureservice.security.model.CustomUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // API Gateway가 전달한 헤더 읽기
        String userId = request.getHeader("X-User-Id");
        String authority = request.getHeader("X-User-Authority");
        String username = request.getHeader("X-User-Email");

        log.info("userId : {}", userId);
        log.info("authority : {}", authority);
        log.info("username : {}", username);

        if (userId != null && authority != null) {
            // 이미 Gateway에서 검증된 정보로 인증 객체 구성
            CustomUser customUser = CustomUser.builder()
                    .email(username)
                    .userId(Long.valueOf(userId))
                    .authorities(Collections.singleton(new SimpleGrantedAuthority(authority)))
                    .build();

            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(customUser, null,
                            List.of(new SimpleGrantedAuthority(authority)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}