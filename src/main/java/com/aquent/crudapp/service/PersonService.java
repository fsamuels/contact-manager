package com.aquent.crudapp.service;

import com.aquent.crudapp.model.Person;
import java.util.List;

/**
 * Business operations for managing {@link Person} records.
 */
public interface PersonService {

    /** Returns all people, ordered by last then first name. */
    List<Person> listPeople();

    /** Returns the person with the given ID. */
    Person readPerson(Integer personId);

    /**
     * Validates and creates a new person record.
     *
     * @return list of validation error messages; empty if successful
     */
    List<String> createPerson(Person person);

    /**
     * Validates and updates an existing person record.
     *
     * @return list of validation error messages; empty if successful
     */
    List<String> updatePerson(Person person);

    /** Deletes the person with the given ID. */
    void deletePerson(Integer personId);

    /**
     * Validates the fields of a person without persisting.
     *
     * @return list of validation error messages; empty if valid
     */
    List<String> validatePerson(Person person);
}
