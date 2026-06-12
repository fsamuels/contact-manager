package com.aquent.crudapp.dao;

import com.aquent.crudapp.model.Person;
import java.util.List;

/**
 * Data access operations for {@link Person} records.
 */
public interface PersonDao {

    /**
     * Retrieves all person records ordered by last name, then first name.
     */
    List<Person> listPeople();

    /**
     * Retrieves a single person by primary key.
     */
    Person readPerson(Integer personId);

    /**
     * Inserts a new person record and returns the generated ID.
     */
    Integer createPerson(Person person);

    /**
     * Updates an existing person record.
     */
    void updatePerson(Person person);

    /**
     * Deletes a person record by primary key.
     */
    void deletePerson(Integer personId);
}
