package com.example.contactmanager.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Form backing object for adding a note to a person.
 */
public class NoteForm {

    @NotBlank(message = "Note text is required.")
    @Size(max = 1000, message = "Note must be at most 1000 characters.")
    private String noteText;

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
