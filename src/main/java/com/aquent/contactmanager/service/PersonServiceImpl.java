package com.aquent.contactmanager.service;

import com.aquent.contactmanager.dao.PersonDao;
import com.aquent.contactmanager.model.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link PersonService} implementation delegating persistence to a {@link PersonDao}.
 */
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonDao personDao;

    /**
     * Creates the service.
     *
     * @param personDao the data-access object (constructor injection)
     */
    public PersonServiceImpl(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> getAllPeople() {
        return personDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Person getPerson(int id) {
        return personDao.findById(id);
    }

    @Override
    @Transactional
    public int createPerson(Person person) {
        return personDao.create(person);
    }

    @Override
    @Transactional
    public void updatePerson(Person person) {
        personDao.update(person);
    }

    @Override
    @Transactional
    public void deletePerson(int id) {
        personDao.delete(id);
    }
}
