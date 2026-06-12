package com.example.contactmanager.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.example.contactmanager.domain.Person;

/**
 * REST request payload for creating or updating a person. Mirrors the Bean
 * Validation rules on {@link Person} (which remain authoritative for the
 * server-rendered form).
 */
public record PersonRequest(

        @NotBlank(message = "First name is required.")
        @Size(max = 30, message = "First name must be at most 30 characters.")
        String firstName,

        @NotBlank(message = "Last name is required.")
        @Size(max = 30, message = "Last name must be at most 30 characters.")
        String lastName,

        @NotBlank(message = "Email address is required.")
        @Size(max = 30, message = "Email address must be at most 30 characters.")
        String emailAddress,

        @NotBlank(message = "Street address is required.")
        @Size(max = 60, message = "Street address must be at most 60 characters.")
        String streetAddress,

        @NotBlank(message = "City is required.")
        @Size(max = 30, message = "City must be at most 30 characters.")
        String city,

        @NotBlank(message = "State is required.")
        @Pattern(regexp = "[A-Za-z]{2}", message = "State must be exactly 2 letters.")
        String state,

        @NotBlank(message = "Zip code is required.")
        @Pattern(regexp = "\\d{5}", message = "Zip code must be exactly 5 digits.")
        String zipCode) {

    /**
     * @return a new (unsaved) entity populated from this request
     */
    public Person toEntity() {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(emailAddress);
        person.setStreetAddress(streetAddress);
        person.setCity(city);
        person.setState(state);
        person.setZipCode(zipCode);
        return person;
    }
}
