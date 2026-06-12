package com.example.contactmanager.service;

import java.util.UUID;

/**
 * Thrown when an operation references a note that does not exist for the
 * given person (it may have been deleted in another session, or belongs to a
 * different person).
 */
public class NoteNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final UUID personId;

    /**
     * @param personId the person the note was requested for
     * @param noteId   the identifier that could not be found
     */
    public NoteNotFoundException(UUID personId, UUID noteId) {
        super("No note found with id " + noteId);
        this.personId = personId;
    }

    /**
     * @return the person the note was requested for
     */
    public UUID getPersonId() {
        return personId;
    }
}
