package com.example.contactmanager.service;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.contactmanager.domain.Page;
import com.example.contactmanager.domain.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link DefaultPersonService} on top of the real
 * repository and an embedded H2 database. Each test runs in a rolled-back
 * transaction against an initially empty schema.
 */
@DataJpaTest
@Import(DefaultPersonService.class)
class DefaultPersonServiceTest {

    @Autowired
    private PersonService personService;

    private static Person newPerson(String firstName, String lastName) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(firstName.toLowerCase() + "@example.com");
        person.setStreetAddress("123 Main St");
        person.setCity("Springfield");
        person.setState("ma");
        person.setZipCode("01101");
        return person;
    }

    @Test
    void createPersonAssignsIdAndNormalizesState() {
        UUID id = personService.createPerson(newPerson("Jane", "Doe"));
        Person saved = personService.findPerson(id).orElseThrow();
        assertEquals("MA", saved.getState(), "State abbreviation is normalized to uppercase");
    }

    @Test
    void listPeopleClampsPageNumberToValidRange() {
        for (int i = 0; i < 11; i++) {
            personService.createPerson(newPerson("Jane" + i, "Doe"));
        }

        Page<Person> pastEnd = personService.listPeople(99, 10);
        assertEquals(2, pastEnd.pageNumber());
        assertEquals(1, pastEnd.items().size());

        Page<Person> belowStart = personService.listPeople(0, 10);
        assertEquals(1, belowStart.pageNumber());
        assertEquals(10, belowStart.items().size());
        assertEquals(11, belowStart.totalItems());
    }

    @Test
    void listPeopleRejectsNonPositivePageSize() {
        assertThrows(IllegalArgumentException.class, () -> personService.listPeople(1, 0));
    }

    @Test
    void updatePersonModifiesExistingRecord() {
        UUID id = personService.createPerson(newPerson("Jane", "Doe"));

        Person updated = newPerson("Janet", "Doe");
        updated.setId(id);
        personService.updatePerson(updated);

        assertEquals("Janet", personService.findPerson(id).orElseThrow().getFirstName());
    }

    @Test
    void updateMissingPersonThrowsNotFound() {
        Person person = newPerson("Jane", "Doe");
        person.setId(UUID.randomUUID());
        assertThrows(PersonNotFoundException.class, () -> personService.updatePerson(person));
    }

    @Test
    void deletePersonRemovesRecord() {
        UUID id = personService.createPerson(newPerson("Jane", "Doe"));
        personService.deletePerson(id);
        assertTrue(personService.findPerson(id).isEmpty());
    }

    @Test
    void deleteMissingPersonThrowsNotFound() {
        assertThrows(PersonNotFoundException.class, () -> personService.deletePerson(UUID.randomUUID()));
    }
}
