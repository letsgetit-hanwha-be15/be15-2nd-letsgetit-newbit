package com.newbit.newbitfeatureservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.newbit.newbitfeatureservice.client")
public class NewbitMainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewbitMainServiceApplication.class, args);
    }

}
