package com.example.demo.user.person.dao;

import com.example.demo.user.person.model.Persons;

public interface PersonsDAO {
    Persons getPersonById(String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsPersonById(String id);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Persons getPersonByEmail(String email);
}
