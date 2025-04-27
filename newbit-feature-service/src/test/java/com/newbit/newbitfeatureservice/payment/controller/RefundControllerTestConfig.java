package com.newbit.newbitfeatureservice.payment.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.newbit.newbitfeatureservice.payment.controller.AbstractApiController;

@TestConfiguration
@Import(AbstractApiController.class)
public class RefundControllerTestConfig {
    
    @Bean
    public AbstractApiController abstractApiController() {
        return new AbstractApiController() {};
    }
} 