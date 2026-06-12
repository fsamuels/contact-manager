package com.example.contactmanager.api;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.contactmanager.domain.Note;

/**
 * REST representation of a note.
 */
public record NoteDto(
        UUID id,
        UUID personId,
        String noteText,
        LocalDateTime createdAt) {

    /**
     * @param note the note entity
     * @return the REST representation
     */
    public static NoteDto of(Note note) {
        return new NoteDto(note.getId(), note.getPerson().getId(), note.getNoteText(), note.getCreatedAt());
    }
}
