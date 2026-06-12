package com.example.contactmanager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.contactmanager.domain.Person;

/**
 * Spring Data JPA repository for {@link Person} records. CRUD, sorting, and
 * pagination are inherited from {@link JpaRepository}; query methods for
 * future relations can be added here as derived queries.
 */
public interface PersonRepository extends JpaRepository<Person, UUID> {
}
