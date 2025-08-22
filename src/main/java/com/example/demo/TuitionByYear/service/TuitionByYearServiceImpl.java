package com.example.demo.TuitionByYear.service;

import com.example.demo.TuitionByYear.dao.TuitionByYearDAO;
import com.example.demo.TuitionByYear.model.TuitionByYear;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TuitionByYearServiceImpl implements TuitionByYearService {
    @Override
    public List<TuitionByYear> getTuitionsByYear(Integer admissionYear) {
        return tuitionByYearDAO.getTuitionsByYear(admissionYear);
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
