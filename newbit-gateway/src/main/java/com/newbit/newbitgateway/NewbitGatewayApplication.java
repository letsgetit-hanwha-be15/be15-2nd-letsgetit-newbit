package com.newbit.newbitgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NewbitGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewbitGatewayApplication.class, args);
    }

}
