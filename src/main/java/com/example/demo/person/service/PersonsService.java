package com.example.demo.person.service;

import com.example.demo.person.model.Persons;

public interface PersonsService {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    Persons getPersonById(String id);
}
