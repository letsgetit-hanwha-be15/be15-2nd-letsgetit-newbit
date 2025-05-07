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

        String userId = request.getHeader("X-User-Id");
        String authority = request.getHeader("X-User-Authority");
        String email = request.getHeader("X-User-Email");
        String nickname = request.getHeader("X-User-Nickname");
        String point = request.getHeader("X-User-Point");
        String diamond = request.getHeader("X-User-Diamond");
        String mentorId = request.getHeader("X-Mentor-Id");

        log.info("userId: {}", userId);
        log.info("authority: {}", authority);
        log.info("email: {}", email);
        log.info("nickname: {}", nickname);
        log.info("point: {}", point);
        log.info("diamond: {}", diamond);
        log.info("mentorId: {}", mentorId);

        if (userId != null && authority != null) {
            CustomUser customUser = CustomUser.builder()
                    .userId(Long.valueOf(userId))
                    .email(email)
                    .nickname(nickname)
                    .point(point != null ? Integer.parseInt(point) : null)
                    .diamond(diamond != null ? Integer.parseInt(diamond) : null)
                    .mentorId(mentorId != null ? Long.parseLong(mentorId) : null)
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