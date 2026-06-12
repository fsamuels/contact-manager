package com.example.contactmanager.service;

/**
 * Thrown when an operation references a note that does not exist for the
 * given person (it may have been deleted in another session, or belongs to a
 * different person).
 */
public class NoteNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final long personId;

    /**
     * @param personId the person the note was requested for
     * @param noteId   the identifier that could not be found
     */
    public NoteNotFoundException(long personId, long noteId) {
        super("No note found with id " + noteId);
        this.personId = personId;
    }

    /**
     * @return the person the note was requested for
     */
    public long getPersonId() {
        return personId;
    }
}
