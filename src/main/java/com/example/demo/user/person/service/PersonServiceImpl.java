package com.example.demo.user.person.service;

import com.example.demo.user.person.dao.PersonsDAO;
import com.example.demo.user.person.model.Persons;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonsService {
    @Override
    public Persons getPersonByEmail(String email) {
        return personsDAO.getPersonByEmail(email);
    }

    @Override
    public Persons getPersonById(String id) {
        return personsDAO.getPersonById(id);
    }

    private final PersonsDAO personsDAO;

    PersonServiceImpl(PersonsDAO personsDAO) {
        this.personsDAO = personsDAO;
    }

    @Override
    public boolean existsByEmail(String email) {
        return personsDAO.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return personsDAO.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsPersonById(String id) {
        return personsDAO.existsPersonById(id);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        return personsDAO.existsByEmailExcludingId(email, id);
    }

    @Override
    public boolean existsByPhoneNumberExcludingId(String phoneNumber, String id) {
        return personsDAO.existsByPhoneNumberExcludingId(phoneNumber, id);
    }
}
