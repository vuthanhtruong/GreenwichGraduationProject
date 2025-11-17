package com.example.demo.tuitionByYear.service;

import com.example.demo.campus.model.Campuses;
import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.tuitionByYear.dao.TuitionByYearDAO;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;
import com.example.demo.user.admin.service.AdminsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TuitionByYearServiceImpl implements TuitionByYearService {
    @Override
    public List<TuitionByYear> tuitionReferenceForStudentsByCampus(Integer admissionYear, Campuses campus) {
        return tuitionByYearDAO.tuitionReferenceForStudentsByCampus(admissionYear, campus);
    }

    @Override
    public List<MajorSubjects> getMajorSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Majors major, Campuses campus) {
        return tuitionByYearDAO.getMajorSubjectsWithTuitionByYearAndCurriculum(admissionYear, curriculum, major, campus);
    }

    @Override
    public List<SpecializedSubject> getSpecializedSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Majors major, Campuses campus) {
        return tuitionByYearDAO.getSpecializedSubjectsWithTuitionByYearAndCurriculum(admissionYear, curriculum, major, campus);
    }

    @Override
    public List<MinorSubjects> getMinorSubjectsWithTuitionByYear(Integer admissionYear, Campuses campus) {
        return tuitionByYearDAO.getMinorSubjectsWithTuitionByYear(admissionYear, campus);
    }

    @Override
    public List<Integer> findAllAdmissionYearsWithMinorTuition(Campuses campus) {
        return tuitionByYearDAO.findAllAdmissionYearsWithMinorTuition(campus);
    }

    @Override
    public List<Integer> findAllAdmissionYearsWithSpecializedTuition(String campusId, Majors major) {
        return tuitionByYearDAO.findAllAdmissionYearsWithSpecializedTuition(campusId, major);
    }


    private final TuitionByYearDAO tuitionByYearDAO;

    public TuitionByYearServiceImpl(TuitionByYearDAO tuitionByYearDAO) {
        this.tuitionByYearDAO = tuitionByYearDAO;
    }

    @Override
    public List<TuitionByYear> tuitionFeesByCampus(String campusId, Integer admissionYear) {
        return tuitionByYearDAO.tuitionFeesByCampus(campusId, admissionYear);
    }

    @Override
    public TuitionByYear findById(TuitionByYearId id) {
        return tuitionByYearDAO.findById(id);
    }

    @Override
    public void updateTuition(TuitionByYear tuition) {
        tuitionByYearDAO.updateTuition(tuition);
    }

    @Override
    public void createTuition(TuitionByYear tuition) {
        tuitionByYearDAO.createTuition(tuition);
    }

    @Override
    public List<TuitionByYear> getTuitionsWithFeeByYearAndCampus(Integer admissionYear, Campuses campus) {
        return tuitionByYearDAO.getTuitionsWithFeeByYearAndCampus(admissionYear, campus);
    }

    @Override
    public List<Integer> findAllAdmissionYearsWithMajorTuition(Campuses campus, Majors major) {
        return tuitionByYearDAO.findAllAdmissionYearsWithMajorTuition(campus, major);
    }

    @Override
    public List<TuitionByYear> getTuitionsWithoutFeeByYear(Integer admissionYear, Campuses campus) {
        return tuitionByYearDAO.getTuitionsWithoutFeeByYear(admissionYear, campus);
    }

    @Override
    public List<TuitionByYear> getTuitionsWithReStudyFeeByYear(Integer admissionYear, Campuses campus) {
        return tuitionByYearDAO.getTuitionsWithReStudyFeeByYear(admissionYear, campus);
    }

    @Override
    public List<TuitionByYear> getTuitionsWithoutReStudyFeeByYear(Integer admissionYear, Campuses campus) {
        return tuitionByYearDAO.getTuitionsWithoutReStudyFeeByYear(admissionYear, campus);
    }

    @Override
    public List<Integer> findAllAdmissionYears(Campuses campus) {
        return tuitionByYearDAO.findAllAdmissionYears(campus);
    }

    @Override
    public List<TuitionByYear> findByAdmissionYear(Integer admissionYear, Campuses campus) {
        return tuitionByYearDAO.findByAdmissionYear(admissionYear, campus);
    }

    @Override
    public void finalizeContracts(Integer admissionYear, Campuses campus) {
        tuitionByYearDAO.finalizeContracts(admissionYear, campus);
    }
}