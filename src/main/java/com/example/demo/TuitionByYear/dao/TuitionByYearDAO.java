package com.example.demo.TuitionByYear.dao;

import com.example.demo.TuitionByYear.model.TuitionByYear;

import java.util.List;

public interface TuitionByYearDAO {
    List<TuitionByYear> findByAdmissionYear(Integer admissionYear);
    List<Integer> findAllAdmissionYears();
    List<Integer> getAllAdmissionYears();
    List<TuitionByYear> getTuitionsByYear(Integer admissionYear);
}
