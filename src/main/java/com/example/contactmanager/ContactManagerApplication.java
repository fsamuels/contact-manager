package com.example.contactmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point. Auto-configuration covers component scanning for
 * this package and below; the embedded H2 database is configured in
 * {@code application.properties}, and the Vue SPA is served from
 * {@code static/app}.
 */
@SpringBootApplication
public class ContactManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactManagerApplication.class, args);
    }
}
