package com.example.contactmanager.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST request payload for adding a note to a person.
 */
public record NoteRequest(

        @NotBlank(message = "Note text is required.")
        @Size(max = 1000, message = "Note must be at most 1000 characters.")
        String noteText) {
}
