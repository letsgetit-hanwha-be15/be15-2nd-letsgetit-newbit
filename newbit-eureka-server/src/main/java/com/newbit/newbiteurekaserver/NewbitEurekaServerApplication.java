package com.newbit.newbiteurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class NewbitEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewbitEurekaServerApplication.class, args);
    }

}
