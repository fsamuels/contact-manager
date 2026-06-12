package com.aquent.contactmanager.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Programmatic replacement for {@code web.xml}.
 *
 * <p>Bootstraps the Spring {@code DispatcherServlet} and root application context using Java
 * configuration. Discovered automatically by the Servlet container via the
 * {@code ServletContainerInitializer} mechanism (Servlet 3.0+).</p>
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /** Root context: data-access and infrastructure beans. */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{DataConfig.class};
    }

    /** Servlet context: Spring MVC (controllers, view resolver). */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    /** Map the {@code DispatcherServlet} to the application root. */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
