package com.aquent.crudapp.service;

import com.aquent.crudapp.dao.PersonDao;
import com.aquent.crudapp.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link PersonService}.
 */
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonDao personDao;

    @Override
    @Transactional(readOnly = true)
    public List<Person> listPeople() {
        return personDao.listPeople();
    }

    @Override
    @Transactional(readOnly = true)
    public Person readPerson(Integer personId) {
        return personDao.readPerson(personId);
    }

    @Override
    @Transactional
    public List<String> createPerson(Person person) {
        List<String> errors = validatePerson(person);
        if (errors.isEmpty()) {
            personDao.createPerson(person);
        }
        return errors;
    }

    @Override
    @Transactional
    public List<String> updatePerson(Person person) {
        List<String> errors = validatePerson(person);
        if (errors.isEmpty()) {
            personDao.updatePerson(person);
        }
        return errors;
    }

    @Override
    @Transactional
    public void deletePerson(Integer personId) {
        personDao.deletePerson(personId);
    }

    @Override
    public List<String> validatePerson(Person person) {
        List<String> errors = new ArrayList<String>();

        if (isBlank(person.getFirstName())) {
            errors.add("First name is required.");
        } else if (person.getFirstName().length() > 30) {
            errors.add("First name must be 30 characters or fewer.");
        }

        if (isBlank(person.getLastName())) {
            errors.add("Last name is required.");
        } else if (person.getLastName().length() > 30) {
            errors.add("Last name must be 30 characters or fewer.");
        }

        if (isBlank(person.getEmail())) {
            errors.add("Email address is required.");
        } else if (person.getEmail().length() > 30) {
            errors.add("Email address must be 30 characters or fewer.");
        }

        if (isBlank(person.getStreet())) {
            errors.add("Street address is required.");
        } else if (person.getStreet().length() > 60) {
            errors.add("Street address must be 60 characters or fewer.");
        }

        if (isBlank(person.getCity())) {
            errors.add("City is required.");
        } else if (person.getCity().length() > 30) {
            errors.add("City must be 30 characters or fewer.");
        }

        if (isBlank(person.getState())) {
            errors.add("State is required.");
        } else if (!person.getState().matches("[A-Za-z]{2}")) {
            errors.add("State must be exactly 2 letters.");
        }

        if (isBlank(person.getZip())) {
            errors.add("Zip code is required.");
        } else if (!person.getZip().matches("\\d{5}")) {
            errors.add("Zip code must be exactly 5 digits.");
        }

        return errors;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
