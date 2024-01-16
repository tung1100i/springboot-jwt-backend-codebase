package com.sapo.mock.techshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSpringConfigured
public class TechShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechShopApplication.class, args);
    }

}
