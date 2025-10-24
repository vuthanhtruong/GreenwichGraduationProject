package com.example.demo.specialization.dao;

import com.example.demo.specialization.model.Specialization;
import com.example.demo.major.model.Majors;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpecializationDAO {

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

    List<Specialization> getPaginated(int firstResult, int pageSize, Majors major);

    long numberOfSpecializations(Majors major);
    Map<String, String> specializationValidation(Specialization specialization);

    List<Specialization> specializationByMajor(Majors major);
}
