package com.newbit.newbituserservice.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            /* 현재 요청의 Http Servlet Request 를 가져옴 */
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(requestAttributes != null) {

                // 1. Gateway를 요청하는 상황
                /* 현재 요청의 Authorization 헤더 추출 (Bearer 토큰) */
/*                String authorizationHeader = requestAttributes
                        .getRequest()
                        .getHeader(HttpHeaders.AUTHORIZATION);


                if(authorizationHeader != null) {       // 토큰을 들고 왔다면

                    *//* Feign client 요청에 "Authorization" 헤더 추가 *//*
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationHeader);

                }*/

                // 2. 내부 user service를 요청하는 상황
                String userId = requestAttributes.getRequest().getHeader("X-User-Id");
                String authority = requestAttributes.getRequest().getHeader("X-User-Authority");
                String username = requestAttributes.getRequest().getHeader("X-User-Email");
                requestTemplate.header("X-User-Id", userId);
                requestTemplate.header("X-User-Authority", authority);
                requestTemplate.header("X-User-Email", username);
            }
        };
    }
}