package com.example.contactmanager.api;

import java.util.UUID;

import com.example.contactmanager.domain.Person;

/**
 * REST representation of a person, including the number of active notes.
 */
public record PersonDto(
        UUID id,
        String firstName,
        String lastName,
        String emailAddress,
        String streetAddress,
        String city,
        String state,
        String zipCode,
        long noteCount) {

    /**
     * @param person    the person entity
     * @param noteCount the person's active note count
     * @return the REST representation
     */
    public static PersonDto of(Person person, long noteCount) {
        return new PersonDto(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmailAddress(),
                person.getStreetAddress(),
                person.getCity(),
                person.getState(),
                person.getZipCode(),
                noteCount);
    }
}
