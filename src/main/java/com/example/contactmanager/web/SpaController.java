package com.example.contactmanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the Vue single-page application built into {@code static/app}.
 * Forwards the directory URL to the SPA's index page (Boot's welcome-page
 * handling only covers the application root). Client-side routes can be
 * added here as further forwards when the SPA grows.
 */
@Controller
public class SpaController {

    /**
     * Forwards {@code /app} and {@code /app/} to the SPA entry point.
     */
    @GetMapping({ "/app", "/app/" })
    public String app() {
        return "forward:/app/index.html";
    }
}
