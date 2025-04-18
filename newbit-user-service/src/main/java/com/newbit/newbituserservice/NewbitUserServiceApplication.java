package com.newbit.newbituserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.newbit.newbituserservice.client")
public class NewbitUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewbitUserServiceApplication.class, args);
    }

}
