package com.example.contactmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Spring Boot entry point. Auto-configuration replaces the previous explicit
 * configuration classes: component scanning covers this package and below,
 * the embedded H2 database and JSP view resolution are configured in
 * {@code application.properties}, and static resources are served from the
 * webapp root.
 *
 * <p>Extends {@link SpringBootServletInitializer} so the WAR remains
 * deployable to a standalone servlet container as well as runnable via
 * {@code java -jar}.</p>
 */
@SpringBootApplication
public class ContactManagerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ContactManagerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ContactManagerApplication.class);
    }
}
