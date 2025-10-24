package com.example.demo.tuitionByYear.dao;

import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;

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