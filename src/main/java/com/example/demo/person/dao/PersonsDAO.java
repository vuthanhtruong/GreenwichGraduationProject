package com.example.demo.person.dao;

import com.example.demo.person.model.Persons;

public interface PersonsDAO {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    Persons getPersonById(String id);
}
