package com.example.contactmanager.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.contactmanager.domain.Page;
import com.example.contactmanager.domain.Person;
import com.example.contactmanager.repository.PersonRepository;

/**
 * Default {@link PersonService} implementation delegating persistence to the
 * Spring Data JPA repository.
 */
@Service
@Transactional
public class DefaultPersonService implements PersonService {

    /** Listing order: last name, first name, then id as a stable tiebreaker. */
    private static final Sort LISTING_SORT = Sort.by("lastName", "firstName", "id");

    private final PersonRepository personRepository;

    /**
     * @param personRepository the person repository
     */
    public DefaultPersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> listPeople() {
        return personRepository.findAll(LISTING_SORT);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Person> listPeople(int pageNumber, int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be positive: " + pageSize);
        }
        long totalItems = personRepository.count();
        int totalPages = (int) Math.max(1, (totalItems + pageSize - 1) / pageSize);
        int page = Math.clamp(pageNumber, 1, totalPages);
        List<Person> items = personRepository
                .findAll(PageRequest.of(page - 1, pageSize, LISTING_SORT))
                .getContent();
        return new Page<>(items, page, pageSize, totalItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findPerson(long id) {
        return personRepository.findById(id);
    }

    @Override
    public long createPerson(Person person) {
        normalize(person);
        return personRepository.save(person).getId();
    }

    @Override
    public void updatePerson(Person person) {
        if (!personRepository.existsById(person.getId())) {
            throw new PersonNotFoundException(person.getId());
        }
        normalize(person);
        personRepository.save(person);
    }

    @Override
    public void deletePerson(long id) {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException(id);
        }
        personRepository.deleteById(id);
    }

    /**
     * Normalizes user-entered data before persistence: the state abbreviation
     * is stored in uppercase regardless of how it was typed.
     */
    private static void normalize(Person person) {
        if (person.getState() != null) {
            person.setState(person.getState().toUpperCase(Locale.ROOT));
        }
    }
}
