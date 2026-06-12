package com.aquent.contactmanager.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Domain model representing a single person (contact) record.
 *
 * <p>The Bean Validation constraints declared on the fields mirror the rules in the
 * project specification and are enforced on the server side whenever the object is
 * bound from a submitted form. The same rules are additionally enforced on the client
 * via HTML5 attributes for a better user experience.</p>
 */
public class Person {

    /** Database-generated identifier. {@code null} for a person that has not yet been persisted. */
    private Integer id;

    @NotBlank(message = "First name is required.")
    @Size(max = 30, message = "First name must be at most 30 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 30, message = "Last name must be at most 30 characters.")
    private String lastName;

    @NotBlank(message = "Email address is required.")
    @Size(max = 30, message = "Email address must be at most 30 characters.")
    private String emailAddress;

    @NotBlank(message = "Street address is required.")
    @Size(max = 60, message = "Street address must be at most 60 characters.")
    private String streetAddress;

    @NotBlank(message = "City is required.")
    @Size(max = 30, message = "City must be at most 30 characters.")
    private String city;

    @NotBlank(message = "State is required.")
    @Pattern(regexp = "[A-Za-z]{2}", message = "State must be exactly 2 letters.")
    private String state;

    @NotBlank(message = "Zip code is required.")
    @Pattern(regexp = "\\d{5}", message = "Zip code must be exactly 5 digits.")
    private String zipCode;

    /** Creates an empty person. Required for form binding. */
    public Person() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Person{id=" + id
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", emailAddress='" + emailAddress + '\''
                + ", streetAddress='" + streetAddress + '\''
                + ", city='" + city + '\''
                + ", state='" + state + '\''
                + ", zipCode='" + zipCode + '\''
                + '}';
    }
}
