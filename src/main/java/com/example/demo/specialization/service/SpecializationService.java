package com.example.demo.specialization.service;

import com.example.demo.specialization.model.Specialization;
import com.example.demo.major.model.Majors;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Service
public interface SpecializationService {
    List<Specialization> searchSpecializations(String searchType, String keyword, int firstResult, int pageSize, Majors major);

    long countSearchResults(String searchType, String keyword, Majors major);

    boolean existsByNameExcludingId(String name, String id);

    void addSpecialization(Specialization specialization);

    Specialization getSpecializationById(String id);

    Specialization getSpecializationByName(String name);

    List<Specialization> specializationsByMajor(Majors major);

    Specialization editSpecialization(String id, Specialization specialization);

    void deleteSpecialization(String id);

    String generateUniqueId(String majorId, LocalDate createdDate);

    Map<String, String> specializationValidation(Specialization specialization); // Added method

    List<Specialization> getPaginated(int firstResult, int pageSize, Majors major);

    long numberOfSpecializations(Majors major);

    List<Specialization> specializationByMajor(Majors major);
}