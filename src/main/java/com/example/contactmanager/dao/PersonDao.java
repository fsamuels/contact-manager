package com.example.contactmanager.dao;

import java.util.List;
import java.util.Optional;

import com.example.contactmanager.domain.Person;

/**
 * Data access operations for {@link Person} records.
 */
public interface PersonDao {

    /**
     * Retrieves all person records, ordered by last name, first name, and id.
     *
     * @return all persons, or an empty list if none exist
     */
    List<Person> findAll();

    /**
     * Retrieves a single person by id.
     *
     * @param id the person id
     * @return the person, or empty if no record exists with the given id
     */
    Optional<Person> findById(long id);

    /**
     * Inserts a new person record.
     *
     * @param person the person to insert; the id field is ignored
     * @return the generated id of the new record
     */
    long insert(Person person);

    /**
     * Updates an existing person record identified by {@link Person#getId()}.
     *
     * @param person the person to update; must have a non-null id
     * @throws PersonNotFoundException if no record exists with the person's id
     */
    void update(Person person);

    /**
     * Deletes a person record by id.
     *
     * @param id the person id
     * @throws PersonNotFoundException if no record exists with the given id
     */
    void delete(long id);
}
