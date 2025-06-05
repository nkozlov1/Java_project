package ru.kozlov;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class OwnerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OwnerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}