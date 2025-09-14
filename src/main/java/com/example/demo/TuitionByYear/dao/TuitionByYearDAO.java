package com.example.demo.TuitionByYear.dao;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.model.TuitionByYearId;

import java.util.List;

public interface TuitionByYearDAO {
    List<TuitionByYear> findByAdmissionYear(Integer admissionYear);
    List<Integer> findAllAdmissionYears();
    List<Integer> getAllAdmissionYears();
    List<TuitionByYear> getTuitionsByYear(Integer admissionYear);
    TuitionByYear findById(TuitionByYearId id);
    void updateTuition(TuitionByYear tuition);
    void createTuition(TuitionByYear tuition);
}
