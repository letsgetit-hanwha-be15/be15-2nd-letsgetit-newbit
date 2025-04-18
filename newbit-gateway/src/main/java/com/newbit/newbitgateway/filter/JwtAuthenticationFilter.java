package com.newbit.newbitgateway.filter;

import com.newbit.newbitgateway.jwt.GatewayJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final GatewayJwtTokenProvider jwtTokenProvider;

    /* Reactive Gateway 에서는 WebFlux 기술이 사용 된다.
    비동기/논블로킹 특징으로 대규모 어플리케이션에서 성능적인 부분이 좋다.
    */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 헤더에서 "Authorization" 값을 읽어온다.
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // 만약 토큰이 없거나, "Bearer "로 시작하지 않으면 다음 체인으로 요청을 전달한다.
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        // "Bearer " 접두어를 제거하고 순수 JWT 토큰만 추출한다.
        String token = authHeader.substring(7);

        // JWT 토큰의 유효성을 확인한다.
        if(!jwtTokenProvider.validateToken(token)) {
            // 유효하지 않다면 401 상태 코드를 응답한다.
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 토큰에서 ID와 Role 정보를 추출한다.
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);
        String authority = jwtTokenProvider.getAuthorityFromJWT(token);
        String username = jwtTokenProvider.getUsernameFromJWT(token);

        // 기존 요청 객체를 복제(mutate)하고, 헤더에 정보를 추가한다.
        ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Authority", authority)
                .header("X-User-Email", username)
                .build();

        // 변경 된 요청 객체를 포함하는 새로운 ServerWebExchange를 생성한다.
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutateRequest).build();

        // 다음 필터로 요청 전달한다.
        return chain.filter(mutatedExchange);
    }

    /* GlobalFilter (전역 필터) 의 우선 순위를 지정한다.
     * 숫자가 작을 수록 높은 우선 순위를 가진다. */
    @Override
    public int getOrder() {
        return -1;
    }
}