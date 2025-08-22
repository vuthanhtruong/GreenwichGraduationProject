package com.example.demo.TuitionByYear.service;

import com.example.demo.TuitionByYear.model.TuitionByYear;

import java.util.List;

public interface TuitionByYearService {
    List<TuitionByYear> findByAdmissionYear(Integer admissionYear);
    List<Integer> findAllAdmissionYears();
    List<Integer> getAllAdmissionYears();
    List<TuitionByYear> getTuitionsByYear(Integer admissionYear);
}
