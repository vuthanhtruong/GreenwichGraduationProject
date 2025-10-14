package com.example.demo.person.service;

import com.example.demo.person.model.Persons;

public interface PersonsService {
    Persons getPersonById(String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsPersonById(String id);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Persons getPersonByEmail(String email);
}
