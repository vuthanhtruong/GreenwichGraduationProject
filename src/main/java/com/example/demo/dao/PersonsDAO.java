package com.example.demo.dao;

import com.example.demo.entity.AbstractClasses.Persons;

public interface PersonsDAO {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    Persons getPersonById(String id);
}
