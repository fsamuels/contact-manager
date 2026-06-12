package com.example.contactmanager.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.example.contactmanager.domain.Note;

/**
 * Business operations for managing {@link Note} records. Notes can only be
 * added and (soft) deleted, never edited.
 */
public interface NoteService {

    /**
     * Lists a person's active notes, newest first.
     *
     * @param personId the person id
     * @return the person's notes
     * @throws PersonNotFoundException if the person does not exist
     */
    List<Note> listNotes(long personId);

    /**
     * Adds a note to a person.
     *
     * @param personId the person id
     * @param noteText the validated note text
     * @return the generated id of the new note
     * @throws PersonNotFoundException if the person does not exist
     */
    long addNote(long personId, String noteText);

    /**
     * Soft-deletes a person's note. The row is retained with its deleted flag
     * set and no longer appears anywhere in the application.
     *
     * @param personId the person id the note must belong to
     * @param noteId   the note id
     * @throws NoteNotFoundException if the note does not exist or belongs to
     *                               a different person
     */
    void deleteNote(long personId, long noteId);

    /**
     * Counts active notes for each of the given people.
     *
     * @param personIds the person ids
     * @return a map with one entry per requested id (0 for people without notes)
     */
    Map<Long, Long> countNotes(Collection<Long> personIds);
}
