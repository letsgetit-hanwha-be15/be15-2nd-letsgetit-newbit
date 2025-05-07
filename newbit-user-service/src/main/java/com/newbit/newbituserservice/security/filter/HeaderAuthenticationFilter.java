package com.newbit.newbituserservice.security.filter;

import com.newbit.newbituserservice.security.model.CustomUser;
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

        String userIdStr = request.getHeader("X-User-Id");
        String authority = request.getHeader("X-User-Authority");
        String email = request.getHeader("X-User-Email");
        String nickname = request.getHeader("X-User-Nickname");
        String pointStr = request.getHeader("X-User-Point");
        String diamondStr = request.getHeader("X-User-Diamond");
        String mentorIdStr = request.getHeader("X-Mentor-Id");

        log.info("userId: {}", userIdStr);
        log.info("authority: {}", authority);
        log.info("email: {}", email);
        log.info("nickname: {}", nickname);
        log.info("point: {}", pointStr);
        log.info("diamond: {}", diamondStr);
        log.info("mentorId: {}", mentorIdStr);

        if (userIdStr != null && !userIdStr.equals("null") && authority != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                Long mentorId = (mentorIdStr != null && !mentorIdStr.equals("null")) ? Long.parseLong(mentorIdStr) : null;
                Integer point = (pointStr != null && !pointStr.equals("null")) ? Integer.parseInt(pointStr) : null;
                Integer diamond = (diamondStr != null && !diamondStr.equals("null")) ? Integer.parseInt(diamondStr) : null;

                CustomUser customUser = CustomUser.builder()
                        .userId(userId)
                        .email(email)
                        .nickname(nickname)
                        .point(point)
                        .diamond(diamond)
                        .mentorId(mentorId)
                        .authorities(Collections.singleton(new SimpleGrantedAuthority(authority)))
                        .build();

                PreAuthenticatedAuthenticationToken authentication =
                        new PreAuthenticatedAuthenticationToken(customUser, null,
                                List.of(new SimpleGrantedAuthority(authority)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (NumberFormatException e) {
                log.warn("헤더에서 숫자 파싱 실패: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

}