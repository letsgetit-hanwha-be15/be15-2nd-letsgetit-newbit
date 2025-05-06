package com.newbit.newbitgateway.filter;

import com.newbit.newbitgateway.error.JwtErrorCode;
import com.newbit.newbitgateway.jwt.GatewayJwtTokenProvider;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final GatewayJwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange); // 토큰 없으면 통과
        }

        String token = authHeader.substring(7); // Bearer 떼고 토큰만 추출

        jwtTokenProvider.validateToken(token);

        Long userId = jwtTokenProvider.getUserIdFromJWT(token);
        Long mentorId = jwtTokenProvider.getMentorIdFromJWT(token);
        String authority = jwtTokenProvider.getAuthorityFromJWT(token);
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        String nickname = jwtTokenProvider.getNicknameFromJWT(token);
        Integer point = jwtTokenProvider.getPointFromJWT(token);
        Integer diamond = jwtTokenProvider.getDiamondFromJWT(token);


        ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-mentorId", String.valueOf(mentorId))
                .header("X-User-Authority", authority)
                .header("X-User-Email", username)
                .header("X-User-Nickname", nickname)
                .header("X-User-Point", point.toString())
                .header("X-User-Diamond", diamond.toString())

                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutateRequest).build();
        return chain.filter(mutatedExchange);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, JwtErrorCode errorCode) {
        exchange.getResponse().setStatusCode(errorCode.getHttpStatus());
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorJson = String.format(
                "{\"code\":\"%s\", \"message\":\"%s\"}",
                errorCode.getCode(),
                errorCode.getMessage()
        );

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer buffer = bufferFactory.wrap(errorJson.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // 우선순위 최상
    }
}
