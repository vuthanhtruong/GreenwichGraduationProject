package com.example.demo.TuitionByYear.dao;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.model.TuitionByYearId;

import java.util.List;

public interface TuitionByYearDAO {
    List<TuitionByYear> tuitionFeesByCampus(String campusId, Integer admissionYear);
    TuitionByYear findById(TuitionByYearId id);
    void updateTuition(TuitionByYear tuition);
    void createTuition(TuitionByYear tuition);
    List<TuitionByYear> getTuitionsWithFeeByYear(Integer admissionYear);
    List<TuitionByYear> getTuitionsWithoutFeeByYear(Integer admissionYear);
    List<TuitionByYear> getTuitionsWithReStudyFeeByYear(Integer admissionYear);
    List<TuitionByYear> getTuitionsWithoutReStudyFeeByYear(Integer admissionYear);
    List<Integer> findAllAdmissionYears();
    List<TuitionByYear> findByAdmissionYear(Integer admissionYear);
    void finalizeContracts(Integer admissionYear);
}