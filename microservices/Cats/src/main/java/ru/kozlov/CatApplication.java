package ru.kozlov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CatApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(CatApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}