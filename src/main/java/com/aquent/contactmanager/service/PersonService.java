package com.aquent.contactmanager.service;

import com.aquent.contactmanager.model.Person;

import java.util.List;

/**
 * Business-facing service for managing {@link Person} records.
 *
 * <p>Sits between the web (controller) layer and the data-access layer, providing a stable
 * façade for the application's use cases. Kept thin for this CRUD application, but it is the
 * natural home for any future cross-cutting concerns such as transactions or auditing.</p>
 */
public interface PersonService {

    /** @return all people, ordered for display; never {@code null} */
    List<Person> getAllPeople();

    /**
     * @param id the person identifier
     * @return the matching person, or {@code null} if none exists
     */
    Person getPerson(int id);

    /**
     * Persists a new person.
     *
     * @param person the person to create
     * @return the generated identifier
     */
    int createPerson(Person person);

    /**
     * Saves changes to an existing person.
     *
     * @param person the person to update
     */
    void updatePerson(Person person);

    /**
     * Removes a person.
     *
     * @param id the identifier of the person to delete
     */
    void deletePerson(int id);
}
