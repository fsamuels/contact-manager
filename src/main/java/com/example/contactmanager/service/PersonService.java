package com.example.contactmanager.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;

import com.example.contactmanager.domain.Page;
import com.example.contactmanager.domain.Person;

/**
 * Business operations for managing {@link Person} records.
 */
public interface PersonService {

    /**
     * Lists all persons in the system.
     *
     * @return all persons ordered by last name then first name
     */
    List<Person> listPeople();

    /**
     * Lists a single page of persons.
     *
     * <p>The requested page number is clamped to the valid range, so a value
     * below 1 yields the first page and a value past the end yields the last
     * page.</p>
     *
     * @param pageNumber the requested 1-based page number
     * @param pageSize   the maximum number of persons per page; must be positive
     * @return the requested page, ordered by last name then first name
     */
    Page<Person> listPeople(int pageNumber, int pageSize);

    /**
     * Lists a single page of persons using the provided sort.
     *
     * @param pageNumber the requested 1-based page number
     * @param pageSize   the maximum number of persons per page; must be positive
     * @param sort       the sort to apply
     * @return the requested page
     */
    Page<Person> listPeople(int pageNumber, int pageSize, Sort sort);

    /**
     * Finds a single person by id.
     *
     * @param id the person id
     * @return the person, or empty if not found
     */
    Optional<Person> findPerson(UUID id);

    /**
     * Creates a new person record.
     *
     * @param person the validated person data; the id field is ignored
     * @return the generated id of the new record
     */
    UUID createPerson(Person person);

    /**
     * Updates an existing person record.
     *
     * @param person the validated person data; must have a non-null id
     * @throws PersonNotFoundException if the person no longer exists
     */
    void updatePerson(Person person);

    /**
     * Deletes a person record by id.
     *
     * @param id the person id
     * @throws PersonNotFoundException if the person no longer exists
     */
    void deletePerson(UUID id);
}
