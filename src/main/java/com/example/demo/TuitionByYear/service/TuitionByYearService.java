package com.example.demo.TuitionByYear.service;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.model.TuitionByYearId;

import java.util.List;

public interface TuitionByYearService {
    List<TuitionByYear> findByAdmissionYear(Integer admissionYear);
    List<Integer> findAllAdmissionYears();
    List<Integer> getAllAdmissionYears();
    List<TuitionByYear> getTuitionsWithFeeByYear(Integer admissionYear);
    List<TuitionByYear> getTuitionsWithoutFeeByYear(Integer admissionYear);
    TuitionByYear findById(TuitionByYearId id);
    void updateTuition(TuitionByYear tuition);
    void createTuition(TuitionByYear tuition);

}
