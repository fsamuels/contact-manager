package com.example.contactmanager.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.contactmanager.dao.PersonDao;
import com.example.contactmanager.domain.Person;

/**
 * Default {@link PersonService} implementation delegating persistence to the
 * DAO layer.
 */
@Service
@Transactional
public class DefaultPersonService implements PersonService {

    private final PersonDao personDao;

    /**
     * @param personDao the person DAO
     */
    public DefaultPersonService(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> listPeople() {
        return personDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findPerson(long id) {
        return personDao.findById(id);
    }

    @Override
    public long createPerson(Person person) {
        normalize(person);
        return personDao.insert(person);
    }

    @Override
    public void updatePerson(Person person) {
        normalize(person);
        personDao.update(person);
    }

    @Override
    public void deletePerson(long id) {
        personDao.delete(id);
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
