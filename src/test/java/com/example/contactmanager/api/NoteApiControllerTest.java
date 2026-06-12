package com.example.contactmanager.api;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.contactmanager.domain.Person;
import com.example.contactmanager.service.NoteService;
import com.example.contactmanager.service.PersonService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc JSON tests for {@link NoteApiController}. Each test runs in a
 * rolled-back transaction against an initially empty database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private NoteService noteService;

    private UUID createSamplePerson() {
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
    void listReturnsNotesNewestFirst() throws Exception {
        UUID personId = createSamplePerson();
        noteService.addNote(personId, "First note.");
        noteService.addNote(personId, "Second note.");

        mockMvc.perform(get("/api/persons/{personId}/notes", personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].noteText").value("Second note."))
                .andExpect(jsonPath("$[1].noteText").value("First note."))
                .andExpect(jsonPath("$[0].personId").value(personId.toString()))
                .andExpect(jsonPath("$[0].createdAt", notNullValue()));
    }

    @Test
    void listForMissingPersonReturnsProblemDetail() throws Exception {
        mockMvc.perform(get("/api/persons/{personId}/notes", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", startsWith("application/problem+json")));
    }

    @Test
    void addNoteReturns201WithLocation() throws Exception {
        UUID personId = createSamplePerson();

        mockMvc.perform(post("/api/persons/{personId}/notes", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"noteText\": \"A fresh note.\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/persons/" + personId + "/notes/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.noteText").value("A fresh note."));
    }

    @Test
    void addBlankNoteReturnsFieldError() throws Exception {
        UUID personId = createSamplePerson();

        mockMvc.perform(post("/api/persons/{personId}/notes", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"noteText\": \"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.noteText").value("Note text is required."));
    }

    @Test
    void deleteNoteReturns204AndHidesNote() throws Exception {
        UUID personId = createSamplePerson();
        UUID noteId = noteService.addNote(personId, "Doomed note.").getId();

        mockMvc.perform(delete("/api/persons/{personId}/notes/{noteId}", personId, noteId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/persons/{personId}/notes", personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteNoteOfAnotherPersonReturns404() throws Exception {
        UUID ownerId = createSamplePerson();
        UUID otherId = createSamplePerson();
        UUID noteId = noteService.addNote(ownerId, "Jane's note.").getId();

        mockMvc.perform(delete("/api/persons/{personId}/notes/{noteId}", otherId, noteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }
}
