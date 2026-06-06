package com.idp.environment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnvironmentServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                EnvironmentServiceApplication.class,
                args
        );
    }
}