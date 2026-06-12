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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc JSON tests for {@link PersonApiController}. Each test runs in a
 * rolled-back transaction against an initially empty database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PersonApiControllerTest {

    private static final String VALID_BODY = """
            {
              "firstName": "Jane",
              "lastName": "Doe",
              "emailAddress": "jane@example.com",
              "streetAddress": "123 Main St",
              "city": "Springfield",
              "state": "ma",
              "zipCode": "01101"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private NoteService noteService;

    private UUID createSamplePerson(String firstName, String lastName) {
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
    void listReturnsPageEnvelope() throws Exception {
        for (int i = 0; i < 11; i++) {
            createSamplePerson("Jane" + i, "Doe");
        }

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(10)))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalItems").value(11))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void listRejectsOutOfRangePageSize() throws Exception {
        mockMvc.perform(get("/api/persons").param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", startsWith("application/problem+json")));
    }

    @Test
    void getReturnsPersonWithNoteCount() throws Exception {
        UUID id = createSamplePerson("Jane", "Doe");
        noteService.addNote(id, "First.");
        noteService.addNote(id, "Second.");

        mockMvc.perform(get("/api/persons/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.noteCount").value(2));
    }

    @Test
    void getMissingPersonReturnsProblemDetail() throws Exception {
        mockMvc.perform(get("/api/persons/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", startsWith("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void malformedIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/persons/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Parameter"));
    }

    @Test
    void createReturns201WithLocationAndNormalizedState() throws Exception {
        mockMvc.perform(post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/persons/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.state").value("MA"))
                .andExpect(jsonPath("$.noteCount").value(0));
    }

    @Test
    void createInvalidBodyReturnsFieldErrors() throws Exception {
        String body = """
                {
                  "firstName": "",
                  "lastName": "Doe",
                  "emailAddress": "jane@example.com",
                  "streetAddress": "123 Main St",
                  "city": "Springfield",
                  "state": "XYZ",
                  "zipCode": "123"
                }
                """;
        mockMvc.perform(post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.firstName").value("First name is required."))
                .andExpect(jsonPath("$.errors.state").value("State must be exactly 2 letters."))
                .andExpect(jsonPath("$.errors.zipCode").value("Zip code must be exactly 5 digits."));
    }

    @Test
    void updateModifiesExistingPerson() throws Exception {
        UUID id = createSamplePerson("Jane", "Doe");
        String body = VALID_BODY.replace("\"Jane\"", "\"Janet\"");

        mockMvc.perform(put("/api/persons/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Janet"));
    }

    @Test
    void updateMissingPersonReturns404() throws Exception {
        mockMvc.perform(put("/api/persons/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204AndPersonIsGone() throws Exception {
        UUID id = createSamplePerson("Jane", "Doe");

        mockMvc.perform(delete("/api/persons/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/persons/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMissingPersonReturns404() throws Exception {
        mockMvc.perform(delete("/api/persons/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
