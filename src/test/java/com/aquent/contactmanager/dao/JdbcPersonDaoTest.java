package com.aquent.contactmanager.dao;

import com.aquent.contactmanager.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link JdbcPersonDao} exercising real SQL against an in-memory H2 database.
 */
class JdbcPersonDaoTest {

    private EmbeddedDatabase database;
    private JdbcPersonDao dao;

    @BeforeEach
    void setUp() {
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("classpath:schema.sql")
                .build();
        dao = new JdbcPersonDao(database);
    }

    @AfterEach
    void tearDown() {
        database.shutdown();
    }

    private Person sample() {
        Person p = new Person();
        p.setFirstName("Test");
        p.setLastName("User");
        p.setEmailAddress("test@example.com");
        p.setStreetAddress("1 Test St");
        p.setCity("Testville");
        p.setState("CA");
        p.setZipCode("90210");
        return p;
    }

    @Test
    void createAssignsIdAndFindByIdReturnsPerson() {
        int id = dao.create(sample());
        Person found = dao.findById(id);

        assertNotNull(found);
        assertEquals(id, found.getId());
        assertEquals("Test", found.getFirstName());
        assertEquals("90210", found.getZipCode());
    }

    @Test
    void findAllReturnsCreatedPeople() {
        dao.create(sample());
        dao.create(sample());

        List<Person> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void updatePersistsChanges() {
        int id = dao.create(sample());
        Person p = dao.findById(id);
        p.setCity("Newtown");
        p.setState("NY");
        dao.update(p);

        Person updated = dao.findById(id);
        assertEquals("Newtown", updated.getCity());
        assertEquals("NY", updated.getState());
    }

    @Test
    void deleteRemovesPerson() {
        int id = dao.create(sample());
        dao.delete(id);

        assertNull(dao.findById(id));
        assertTrue(dao.findAll().isEmpty());
    }
}
