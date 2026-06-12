package com.example.contactmanager.config;

import java.util.Set;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionTrackingMode;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Programmatic replacement for {@code web.xml}. Bootstraps the Spring root and
 * servlet application contexts and maps the {@code DispatcherServlet} to the
 * context root.
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { RootConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebMvcConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // HTML-escape model values rendered through Spring's JSP tags by default.
        servletContext.setInitParameter("defaultHtmlEscape", "true");

        // Track sessions via cookies only; never rewrite jsessionid into URLs.
        servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));

        FilterRegistration.Dynamic encodingFilter =
                servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter("UTF-8", true));
        encodingFilter.addMappingForUrlPatterns(null, false, "/*");

        super.onStartup(servletContext);
    }
}
