package com.example.contactmanager.domain;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the Bean Validation rules on {@link Person} match the project
 * specification.
 */
class PersonValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static Person validPerson() {
        Person person = new Person();
        person.setFirstName("Jane");
        person.setLastName("Doe");
        person.setEmailAddress("jane@example.com");
        person.setStreetAddress("123 Main St");
        person.setCity("Springfield");
        person.setState("MA");
        person.setZipCode("01101");
        return person;
    }

    private static Set<String> invalidProperties(Person person) {
        return validator.validate(person).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void validPersonHasNoViolations() {
        Set<ConstraintViolation<Person>> violations = validator.validate(validPerson());
        assertTrue(violations.isEmpty(), "Expected no violations but got: " + violations);
    }

    @Test
    void blankRequiredFieldsAreRejected() {
        Person person = new Person();
        assertEquals(
                Set.of("firstName", "lastName", "emailAddress", "streetAddress", "city", "state", "zipCode"),
                invalidProperties(person));
    }

    @Test
    void firstNameOver30CharactersIsRejected() {
        Person person = validPerson();
        person.setFirstName("a".repeat(31));
        assertEquals(Set.of("firstName"), invalidProperties(person));
    }

    @Test
    void lastNameOver30CharactersIsRejected() {
        Person person = validPerson();
        person.setLastName("a".repeat(31));
        assertEquals(Set.of("lastName"), invalidProperties(person));
    }

    @Test
    void emailAddressOver30CharactersIsRejected() {
        Person person = validPerson();
        person.setEmailAddress("a".repeat(31));
        assertEquals(Set.of("emailAddress"), invalidProperties(person));
    }

    @Test
    void streetAddressOver60CharactersIsRejected() {
        Person person = validPerson();
        person.setStreetAddress("a".repeat(61));
        assertEquals(Set.of("streetAddress"), invalidProperties(person));
    }

    @Test
    void cityOver30CharactersIsRejected() {
        Person person = validPerson();
        person.setCity("a".repeat(31));
        assertEquals(Set.of("city"), invalidProperties(person));
    }

    @Test
    void stateMustBeExactlyTwoLetters() {
        Person person = validPerson();
        person.setState("M");
        assertEquals(Set.of("state"), invalidProperties(person));
        person.setState("MAS");
        assertEquals(Set.of("state"), invalidProperties(person));
        person.setState("M1");
        assertEquals(Set.of("state"), invalidProperties(person));
        person.setState("ma");
        assertTrue(invalidProperties(person).isEmpty(), "Lowercase letters are accepted and normalized later");
    }

    @Test
    void zipCodeMustBeExactlyFiveDigits() {
        Person person = validPerson();
        person.setZipCode("1234");
        assertEquals(Set.of("zipCode"), invalidProperties(person));
        person.setZipCode("123456");
        assertEquals(Set.of("zipCode"), invalidProperties(person));
        person.setZipCode("12a45");
        assertEquals(Set.of("zipCode"), invalidProperties(person));
    }
}
