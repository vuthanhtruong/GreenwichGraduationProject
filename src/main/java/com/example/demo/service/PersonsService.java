package com.example.demo.service;

import com.example.demo.entity.AbstractClasses.Persons;
import com.example.demo.entity.Students;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonsService {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    Persons getPersonById(String id);
}
