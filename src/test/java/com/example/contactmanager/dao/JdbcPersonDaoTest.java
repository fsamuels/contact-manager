package com.example.contactmanager.dao;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import com.example.contactmanager.domain.Person;
import com.example.contactmanager.testconfig.TestDbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link JdbcPersonDao} against an embedded H2 database.
 * Each test runs in a rolled-back transaction.
 */
@SpringJUnitConfig(TestDbConfig.class)
@Transactional
class JdbcPersonDaoTest {

    @Autowired
    private PersonDao personDao;

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
        assertTrue(personDao.findAll().isEmpty());
    }

    @Test
    void insertAssignsIdAndFindByIdRoundTrips() {
        Person person = newPerson("Jane", "Doe");
        long id = personDao.insert(person);

        Optional<Person> found = personDao.findById(id);
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
        personDao.insert(newPerson("Zoe", "Adams"));
        personDao.insert(newPerson("Amy", "Baker"));
        personDao.insert(newPerson("Al", "Adams"));

        List<Person> people = personDao.findAll();
        assertEquals(3, people.size());
        assertEquals("Al", people.get(0).getFirstName());
        assertEquals("Zoe", people.get(1).getFirstName());
        assertEquals("Amy", people.get(2).getFirstName());
    }

    @Test
    void updateModifiesExistingRecord() {
        long id = personDao.insert(newPerson("Jane", "Doe"));

        Person updated = newPerson("Janet", "Doe");
        updated.setId(id);
        personDao.update(updated);

        Person found = personDao.findById(id).orElseThrow();
        assertEquals("Janet", found.getFirstName());
    }

    @Test
    void updateMissingRecordThrowsNotFound() {
        Person person = newPerson("Jane", "Doe");
        person.setId(9999L);
        assertThrows(PersonNotFoundException.class, () -> personDao.update(person));
    }

    @Test
    void deleteRemovesRecord() {
        long id = personDao.insert(newPerson("Jane", "Doe"));
        personDao.delete(id);
        assertTrue(personDao.findById(id).isEmpty());
    }

    @Test
    void deleteMissingRecordThrowsNotFound() {
        assertThrows(PersonNotFoundException.class, () -> personDao.delete(9999L));
    }
}
