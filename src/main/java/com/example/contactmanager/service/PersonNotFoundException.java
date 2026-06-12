package com.example.contactmanager.service;

/**
 * Thrown when an operation references a person record that does not exist
 * (for example, it was deleted in another session).
 */
public class PersonNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param personId the identifier that could not be found
     */
    public PersonNotFoundException(long personId) {
        super("No person found with id " + personId);
    }
}
