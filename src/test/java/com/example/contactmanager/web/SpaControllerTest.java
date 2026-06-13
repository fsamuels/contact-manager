package com.example.contactmanager.web;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc tests for {@link SpaController}: every SPA entry point and client
 * route must forward to the SPA's index page so deep links and refreshes
 * work.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootRedirectsToSpa() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/"));
    }

    @Test
    void spaEntryPointsForwardToIndex() throws Exception {
        UUID id = UUID.randomUUID();
        String[] paths = {
                "/app",
                "/app/",
                "/app/persons/new",
                "/app/persons/" + id + "/edit",
                "/app/persons/" + id + "/delete",
                "/app/persons/" + id + "/notes",
        };
        for (String path : paths) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("/app/index.html"));
        }
    }
}
