package com.example.demo.TuitionByYear.service;

import com.example.demo.TuitionByYear.dao.TuitionByYearDAO;
import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.model.TuitionByYearId;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TuitionByYearServiceImpl implements TuitionByYearService {
    @Override
    public List<TuitionByYear> getTuitionsWithFeeByYear(Integer admissionYear) {
        return tuitionByYearDAO.getTuitionsWithFeeByYear(admissionYear);
    }

    @Override
    public List<TuitionByYear> getTuitionsWithoutFeeByYear(Integer admissionYear) {
        return tuitionByYearDAO.getTuitionsWithoutFeeByYear(admissionYear);
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
    public List<Integer> getAllAdmissionYears() {
        return tuitionByYearDAO.getAllAdmissionYears();
    }

    private final TuitionByYearDAO  tuitionByYearDAO;

    public TuitionByYearServiceImpl(TuitionByYearDAO tuitionByYearDAO) {
        this.tuitionByYearDAO = tuitionByYearDAO;
    }

    @Override
    public List<TuitionByYear> findByAdmissionYear(Integer admissionYear) {
        return tuitionByYearDAO.findByAdmissionYear(admissionYear);
    }

    @Override
    public List<Integer> findAllAdmissionYears() {
        return tuitionByYearDAO.findAllAdmissionYears();
    }
}
