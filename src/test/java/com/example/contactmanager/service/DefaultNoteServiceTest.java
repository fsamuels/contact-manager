package com.example.contactmanager.service;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.contactmanager.domain.Note;
import com.example.contactmanager.domain.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link DefaultNoteService} on top of the real
 * repositories and an embedded H2 database, including verification that
 * deletion is a soft delete at the row level.
 */
@DataJpaTest
@Import({ DefaultNoteService.class, DefaultPersonService.class })
class DefaultNoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private PersonService personService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private long createPerson(String firstName, String lastName) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(firstName.toLowerCase() + "@example.com");
        person.setStreetAddress("123 Main St");
        person.setCity("Springfield");
        person.setState("MA");
        person.setZipCode("01101");
        return personService.createPerson(person);
    }

    @Test
    void addNoteAndListNotesRoundTrips() {
        long personId = createPerson("Jane", "Doe");
        noteService.addNote(personId, "First note.");
        noteService.addNote(personId, "Second note.");

        List<Note> notes = noteService.listNotes(personId);
        assertEquals(2, notes.size());
        assertEquals("Second note.", notes.get(0).getNoteText(), "Newest note is listed first");
        assertEquals("First note.", notes.get(1).getNoteText());
    }

    @Test
    void deleteNoteIsSoftAndHidesTheNote() {
        long personId = createPerson("Jane", "Doe");
        long noteId = noteService.addNote(personId, "To be deleted.");

        noteService.deleteNote(personId, noteId);
        entityManager.flush();

        assertTrue(noteService.listNotes(personId).isEmpty(), "Soft-deleted note is hidden");
        Boolean deleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM note WHERE note_id = ?", Boolean.class, noteId);
        assertEquals(Boolean.TRUE, deleted, "Row is retained with the deleted flag set");
    }

    @Test
    void addNoteToMissingPersonThrowsNotFound() {
        assertThrows(PersonNotFoundException.class, () -> noteService.addNote(9999L, "Orphan note."));
    }

    @Test
    void deleteNoteBelongingToAnotherPersonThrowsNotFound() {
        long ownerId = createPerson("Jane", "Doe");
        long otherId = createPerson("John", "Smith");
        long noteId = noteService.addNote(ownerId, "Jane's note.");

        assertThrows(NoteNotFoundException.class, () -> noteService.deleteNote(otherId, noteId));
    }

    @Test
    void deleteMissingNoteThrowsNotFound() {
        long personId = createPerson("Jane", "Doe");
        assertThrows(NoteNotFoundException.class, () -> noteService.deleteNote(personId, 9999L));
    }

    @Test
    void countNotesReturnsZeroForPeopleWithoutNotes() {
        long withNotes = createPerson("Jane", "Doe");
        long withoutNotes = createPerson("John", "Smith");
        noteService.addNote(withNotes, "Only note.");

        Map<Long, Long> counts = noteService.countNotes(List.of(withNotes, withoutNotes));
        assertEquals(1L, counts.get(withNotes));
        assertEquals(0L, counts.get(withoutNotes));
    }

    @Test
    void countNotesExcludesSoftDeletedNotes() {
        long personId = createPerson("Jane", "Doe");
        noteService.addNote(personId, "Kept.");
        long deletedId = noteService.addNote(personId, "Removed.");
        noteService.deleteNote(personId, deletedId);

        assertEquals(1L, noteService.countNotes(List.of(personId)).get(personId));
    }

    @Test
    void deletingPersonCascadesToTheirNotes() {
        long personId = createPerson("Jane", "Doe");
        noteService.addNote(personId, "Will go with the person.");
        // Detach the note so the persistence context matches the real flow,
        // where the delete arrives in a fresh request without loaded notes.
        entityManager.flush();
        entityManager.clear();

        personService.deletePerson(personId);
        entityManager.flush();

        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM note WHERE person_id = ?", Integer.class, personId);
        assertEquals(0, remaining, "Hard-deleting a person removes their note rows");
    }
}
