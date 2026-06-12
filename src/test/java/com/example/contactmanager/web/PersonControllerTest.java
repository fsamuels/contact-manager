package com.example.contactmanager.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.example.contactmanager.config.WebMvcConfig;
import com.example.contactmanager.domain.Page;
import com.example.contactmanager.domain.Person;
import com.example.contactmanager.service.PersonService;
import com.example.contactmanager.testconfig.TestDbConfig;

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
 * MockMvc tests for {@link PersonController} running against the real service
 * and DAO layers with an embedded database.
 */
@SpringJUnitWebConfig({ TestDbConfig.class, WebMvcConfig.class })
@Transactional
class PersonControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PersonService personService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

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
    void rootRedirectsToListing() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons"));
    }

    @Test
    void listingShowsPeople() throws Exception {
        createSamplePerson();
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(view().name("person/list"))
                .andExpect(model().attributeExists("people"));
    }

    private void createPeople(int count) {
        for (int i = 0; i < count; i++) {
            createSamplePerson();
        }
    }

    private Page<?> requestListPage(String url) throws Exception {
        return (Page<?>) mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(view().name("person/list"))
                .andReturn()
                .getModelAndView()
                .getModel()
                .get("personPage");
    }

    @Test
    void listingDefaultsToTenPerPage() throws Exception {
        createPeople(11);
        Page<?> page = requestListPage("/persons");
        assertEquals(10, page.getItems().size());
        assertEquals(1, page.getPageNumber());
        assertEquals(10, page.getPageSize());
        assertEquals(11, page.getTotalItems());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void listingSecondPageShowsRemainder() throws Exception {
        createPeople(11);
        Page<?> page = requestListPage("/persons?page=2");
        assertEquals(1, page.getItems().size());
        assertEquals(2, page.getPageNumber());
    }

    @Test
    void listingHonorsOfferedPageSizes() throws Exception {
        createPeople(11);
        Page<?> page = requestListPage("/persons?size=25");
        assertEquals(11, page.getItems().size());
        assertEquals(25, page.getPageSize());
        assertEquals(1, page.getTotalPages());
    }

    @Test
    void listingFallsBackToDefaultSizeForUnsupportedValues() throws Exception {
        createPeople(11);
        Page<?> page = requestListPage("/persons?size=17");
        assertEquals(10, page.getPageSize());
        assertEquals(10, page.getItems().size());
    }

    @Test
    void listingClampsOutOfRangePageNumbers() throws Exception {
        createPeople(11);
        assertEquals(2, requestListPage("/persons?page=99").getPageNumber());
        assertEquals(1, requestListPage("/persons?page=0").getPageNumber());
    }

    @Test
    void createFormRenders() throws Exception {
        mockMvc.perform(get("/persons/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("person/form"))
                .andExpect(model().attributeExists("person"));
    }

    @Test
    void validCreateSavesAndRedirectsToListing() throws Exception {
        mockMvc.perform(post("/persons/new")
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("emailAddress", "john@example.com")
                        .param("streetAddress", "456 Oak Ave")
                        .param("city", "Portland")
                        .param("state", "or")
                        .param("zipCode", "97201"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons"))
                .andExpect(flash().attributeExists("successMessage"));

        Person saved = personService.listPeople().stream()
                .filter(p -> "Smith".equals(p.getLastName()))
                .findFirst()
                .orElseThrow();
        assertEquals("OR", saved.getState(), "State abbreviation is normalized to uppercase");
    }

    @Test
    void invalidCreateRedisplaysFormWithFieldErrors() throws Exception {
        mockMvc.perform(post("/persons/new")
                        .param("firstName", "")
                        .param("lastName", "Smith")
                        .param("emailAddress", "john@example.com")
                        .param("streetAddress", "456 Oak Ave")
                        .param("city", "Portland")
                        .param("state", "ORE")
                        .param("zipCode", "9720"))
                .andExpect(status().isOk())
                .andExpect(view().name("person/form"))
                .andExpect(model().attributeHasFieldErrors("person", "firstName", "state", "zipCode"));
    }

    @Test
    void editFormRendersExistingPerson() throws Exception {
        long id = createSamplePerson();
        mockMvc.perform(get("/persons/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("person/form"))
                .andExpect(model().attributeExists("person"));
    }

    @Test
    void validEditUpdatesAndRedirectsToListing() throws Exception {
        long id = createSamplePerson();
        mockMvc.perform(post("/persons/{id}/edit", id)
                        .param("firstName", "Janet")
                        .param("lastName", "Doe")
                        .param("emailAddress", "janet@example.com")
                        .param("streetAddress", "123 Main St")
                        .param("city", "Springfield")
                        .param("state", "MA")
                        .param("zipCode", "01101"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons"));

        assertEquals("Janet", personService.findPerson(id).orElseThrow().getFirstName());
    }

    @Test
    void deleteConfirmationPageRenders() throws Exception {
        long id = createSamplePerson();
        mockMvc.perform(get("/persons/{id}/delete", id))
                .andExpect(status().isOk())
                .andExpect(view().name("person/delete-confirm"))
                .andExpect(model().attributeExists("person"));
    }

    @Test
    void deleteRemovesPersonAndRedirectsToListing() throws Exception {
        long id = createSamplePerson();
        mockMvc.perform(post("/persons/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons"));

        assertTrue(personService.findPerson(id).isEmpty());
    }

    @Test
    void missingPersonRedirectsToListingWithErrorMessage() throws Exception {
        mockMvc.perform(get("/persons/{id}/edit", 9999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
