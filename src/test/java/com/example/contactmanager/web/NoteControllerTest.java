package com.example.contactmanager.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.contactmanager.domain.Person;
import com.example.contactmanager.service.NoteService;
import com.example.contactmanager.service.PersonService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * MockMvc tests for {@link NoteController} running against the full Boot
 * application context. Each test runs in a rolled-back transaction against
 * an initially empty database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private NoteService noteService;

    private long createSamplePerson() {
        Person person = new Person();
        person.setFirstName("Jane");
        person.setLastName("Doe");
        person.setEmailAddress("jane@example.com");
        person.setStreetAddress("123 Main St");
        person.setCity("Springfield");
        person.setState("MA");
        person.setZipCode("01101");
        return personService.createPerson(person);
    }

    @Test
    void notesPageRenders() throws Exception {
        long personId = createSamplePerson();
        noteService.addNote(personId, "Existing note.");

        mockMvc.perform(get("/persons/{personId}/notes", personId))
                .andExpect(status().isOk())
                .andExpect(view().name("person/notes"))
                .andExpect(model().attributeExists("person", "notes", "noteForm"));
    }

    @Test
    void addNoteSavesAndRedirects() throws Exception {
        long personId = createSamplePerson();

        mockMvc.perform(post("/persons/{personId}/notes", personId)
                        .param("noteText", "A brand new note."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons/" + personId + "/notes"))
                .andExpect(flash().attributeExists("successMessage"));

        assertEquals(1, noteService.listNotes(personId).size());
    }

    @Test
    void blankNoteRedisplaysFormWithFieldError() throws Exception {
        long personId = createSamplePerson();

        mockMvc.perform(post("/persons/{personId}/notes", personId)
                        .param("noteText", "   "))
                .andExpect(status().isOk())
                .andExpect(view().name("person/notes"))
                .andExpect(model().attributeHasFieldErrors("noteForm", "noteText"));

        assertTrue(noteService.listNotes(personId).isEmpty());
    }

    @Test
    void deleteNoteRedirectsAndHidesNote() throws Exception {
        long personId = createSamplePerson();
        long noteId = noteService.addNote(personId, "Doomed note.");

        mockMvc.perform(post("/persons/{personId}/notes/{noteId}/delete", personId, noteId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons/" + personId + "/notes"))
                .andExpect(flash().attributeExists("successMessage"));

        assertTrue(noteService.listNotes(personId).isEmpty());
    }

    @Test
    void missingPersonRedirectsToListingWithErrorMessage() throws Exception {
        mockMvc.perform(get("/persons/{personId}/notes", 9999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void missingNoteRedirectsToNotesWithErrorMessage() throws Exception {
        long personId = createSamplePerson();

        mockMvc.perform(post("/persons/{personId}/notes/{noteId}/delete", personId, 9999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons/" + personId + "/notes"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
