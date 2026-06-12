package com.example.contactmanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the Vue single-page application built into {@code static/app}.
 * Forwards the directory URL to the SPA's index page (Boot's welcome-page
 * handling only covers the application root). Client-side routes are
 * forwarded here so direct navigation and refresh work.
 */
@Controller
public class SpaController {

    /**
     * Forwards SPA entry points and client routes to {@code index.html}.
     */
    @GetMapping({
            "/app",
            "/app/",
            "/app/persons/new",
            "/app/persons/{id}/edit",
            "/app/persons/{id}/delete",
            "/app/persons/{id}/notes"
    })
    public String app() {
        return "forward:/app/index.html";
    }
}
