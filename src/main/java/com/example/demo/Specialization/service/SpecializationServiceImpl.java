package com.example.demo.Specialization.service;

import com.example.demo.Specialization.dao.SpecializationDAO;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.major.model.Majors;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SpecializationServiceImpl implements SpecializationService {
    @Override
    public List<Specialization> specializationByMajor(Majors major) {
        return specializationDAO.specializationByMajor(major);
    }

    private final SpecializationDAO specializationDAO;

    public SpecializationServiceImpl(SpecializationDAO specializationDAO) {
        this.specializationDAO = specializationDAO;
    }

    @Override
    public Map<String, String> specializationValidation(Specialization specialization) {
        return specializationDAO.specializationValidation(specialization);
    }

    @Override
    public List<Specialization> searchSpecializations(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        return specializationDAO.searchSpecializations(searchType, keyword, firstResult, pageSize, major);
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        return specializationDAO.countSearchResults(searchType, keyword, major);
    }

    @Override
    public boolean existsByNameExcludingId(String name, String id) {
        return specializationDAO.existsByNameExcludingId(name, id);
    }

    @Override
    public void addSpecialization(Specialization specialization) {
        specializationDAO.addSpecialization(specialization);
    }

    @Override
    public Specialization getSpecializationById(String id) {
        return specializationDAO.getSpecializationById(id);
    }

    @Override
    public Specialization getSpecializationByName(String name) {
        return specializationDAO.getSpecializationByName(name);
    }

    @Override
    public List<Specialization> specializationsByMajor(Majors major) {
        return specializationDAO.specializationsByMajor(major);
    }

    @Override
    public Specialization editSpecialization(String id, Specialization specialization) {
        return specializationDAO.editSpecialization(id, specialization);
    }

    @Override
    public void deleteSpecialization(String id) {
        specializationDAO.deleteSpecialization(id);
    }

    @Override
    public String generateUniqueId(String majorId, LocalDate createdDate) {
        return specializationDAO.generateUniqueId(majorId, createdDate);
    }

    @Override
    public List<Specialization> getPaginated(int firstResult, int pageSize, Majors major) {
        return specializationDAO.getPaginated(firstResult, pageSize, major);
    }

    @Override
    public long numberOfSpecializations(Majors major) {
        return specializationDAO.numberOfSpecializations(major);
    }
}