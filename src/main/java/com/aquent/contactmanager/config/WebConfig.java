package com.aquent.contactmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Spring MVC configuration: component scanning for the web layer, the JSP view resolver,
 * and static resource handling.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.aquent.contactmanager.controller",
        "com.aquent.contactmanager.service",
        "com.aquent.contactmanager.dao"
})
public class WebConfig implements WebMvcConfigurer {

    /**
     * Resolves logical view names returned by controllers to JSP files under {@code /WEB-INF/views}.
     *
     * @return the configured view resolver
     */
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    /**
     * Serves static assets (CSS, JS) from {@code /resources/}.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }

    /**
     * Delegates unmatched requests (e.g. static files) to the container's default servlet.
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
