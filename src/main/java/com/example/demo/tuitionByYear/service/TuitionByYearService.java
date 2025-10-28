package com.example.demo.tuitionByYear.service;

import com.example.demo.campus.model.Campuses;
import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;

import java.util.List;

public interface TuitionByYearService {
    List<TuitionByYear> tuitionFeesByCampus(String campusId, Integer admissionYear);

    TuitionByYear findById(TuitionByYearId id);

    void updateTuition(TuitionByYear tuition);

    void createTuition(TuitionByYear tuition);

    List<TuitionByYear> getTuitionsWithFeeByYearAndCampus(Integer admissionYear, Campuses campus);

    List<MajorSubjects> getMajorSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Campuses campus);

    List<Integer> findAllAdmissionYearsWithMajorTuition(Campuses campus);

    List<TuitionByYear> getTuitionsWithoutFeeByYear(Integer admissionYear, Campuses campus);

    List<TuitionByYear> getTuitionsWithReStudyFeeByYear(Integer admissionYear, Campuses campus);

    List<TuitionByYear> getTuitionsWithoutReStudyFeeByYear(Integer admissionYear, Campuses campus);

    List<Integer> findAllAdmissionYears(Campuses campus);

    List<TuitionByYear> findByAdmissionYear(Integer admissionYear, Campuses campus);

    void finalizeContracts(Integer admissionYear, Campuses campus);

    List<SpecializedSubject> getSpecializedSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Campuses campus);

    List<Integer> findAllAdmissionYearsWithSpecializedTuition(String campusId);
}
