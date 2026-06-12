package com.example.contactmanager.domain;

import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A person record in the contact manager, mapped to the {@code person} table.
 * Also serves as the web form backing object, carrying the Bean Validation
 * rules defined by the project specification. Camel-case fields map to the
 * snake_case columns via Hibernate's default naming strategy; only the id
 * column needs an explicit name.
 */
@Entity
@Table(name = "person")
public class Person {

    /** Database identifier; {@code null} until the record is persisted. */
    @Id
    @UuidGenerator
    @Column(name = "person_id")
    private UUID id;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person other)) {
            return false;
        }
        return Objects.equals(id, other.id)
                && Objects.equals(firstName, other.firstName)
                && Objects.equals(lastName, other.lastName)
                && Objects.equals(emailAddress, other.emailAddress)
                && Objects.equals(streetAddress, other.streetAddress)
                && Objects.equals(city, other.city)
                && Objects.equals(state, other.state)
                && Objects.equals(zipCode, other.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, emailAddress, streetAddress, city, state, zipCode);
    }

    @Override
    public String toString() {
        return "Person{id=%s, firstName='%s', lastName='%s', emailAddress='%s'}"
                .formatted(id, firstName, lastName, emailAddress);
    }
}
