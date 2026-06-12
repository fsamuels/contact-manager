package com.aquent.contactmanager.dao;

import com.aquent.contactmanager.model.Person;

import java.util.List;

/**
 * Data access abstraction for {@link Person} records.
 *
 * <p>Defined as an interface so the persistence implementation can be swapped (e.g. JDBC,
 * JPA, or an in-memory test double) without affecting the service or web layers.</p>
 */
public interface PersonDao {

    /**
     * Returns all people, ordered by last name then first name.
     *
     * @return an immutable-friendly list of people; never {@code null}, possibly empty
     */
    List<Person> findAll();

    /**
     * Returns the person with the given identifier.
     *
     * @param id the person identifier
     * @return the matching person, or {@code null} if none exists
     */
    Person findById(int id);

    /**
     * Inserts a new person.
     *
     * @param person the person to insert; its {@code id} is ignored
     * @return the generated identifier of the newly inserted person
     */
    int create(Person person);

    /**
     * Updates an existing person.
     *
     * @param person the person to update; identified by its {@code id}
     */
    void update(Person person);

    /**
     * Deletes the person with the given identifier.
     *
     * @param id the identifier of the person to delete
     */
    void delete(int id);
}
