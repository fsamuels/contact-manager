package com.example.contactmanager.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.contactmanager.domain.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link PersonRepository} against an embedded H2
 * database. {@code @DataJpaTest} rolls back each test's transaction; the
 * schema comes from the test {@code application.properties} (no sample data).
 */
@DataJpaTest
class PersonRepositoryTest {

    private static final Sort LISTING_SORT = Sort.by("lastName", "firstName", "id");

    @Autowired
    private PersonRepository personRepository;

    private static Person newPerson(String firstName, String lastName) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(firstName.toLowerCase() + "@example.com");
        person.setStreetAddress("123 Main St");
        person.setCity("Springfield");
        person.setState("MA");
        person.setZipCode("01101");
        return person;
    }

    @Test
    void findAllReturnsEmptyListWhenNoRecords() {
        assertTrue(personRepository.findAll().isEmpty());
    }

    @Test
    void saveAssignsIdAndFindByIdRoundTrips() {
        Person saved = personRepository.save(newPerson("Jane", "Doe"));
        assertNotNull(saved.getId());

        Optional<Person> found = personRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
        assertEquals("jane@example.com", found.get().getEmailAddress());
        assertEquals("123 Main St", found.get().getStreetAddress());
        assertEquals("Springfield", found.get().getCity());
        assertEquals("MA", found.get().getState());
        assertEquals("01101", found.get().getZipCode());
    }

    @Test
    void findAllOrdersByLastNameThenFirstName() {
        personRepository.save(newPerson("Zoe", "Adams"));
        personRepository.save(newPerson("Amy", "Baker"));
        personRepository.save(newPerson("Al", "Adams"));

        List<Person> people = personRepository.findAll(LISTING_SORT);
        assertEquals(3, people.size());
        assertEquals("Al", people.get(0).getFirstName());
        assertEquals("Zoe", people.get(1).getFirstName());
        assertEquals("Amy", people.get(2).getFirstName());
    }

    @Test
    void findAllPageableReturnsRequestedSlice() {
        personRepository.save(newPerson("Zoe", "Adams"));
        personRepository.save(newPerson("Amy", "Baker"));
        personRepository.save(newPerson("Cal", "Carter"));

        List<Person> firstPage = personRepository.findAll(PageRequest.of(0, 2, LISTING_SORT)).getContent();
        assertEquals(2, firstPage.size());
        assertEquals("Adams", firstPage.get(0).getLastName());
        assertEquals("Baker", firstPage.get(1).getLastName());

        List<Person> secondPage = personRepository.findAll(PageRequest.of(1, 2, LISTING_SORT)).getContent();
        assertEquals(1, secondPage.size());
        assertEquals("Carter", secondPage.get(0).getLastName());
    }
}
