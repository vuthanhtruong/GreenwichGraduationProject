package com.example.demo.user.employe.dao;

import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class EmployesDAOImpl implements EmployesDAO {
    @Override
    public MinorEmployes getByMinorId(String id) {
        return entityManager.find(MinorEmployes.class, id);
    }

    @Override
    public MinorEmployes getMinorEmployee() {
        if(deputyStaffsService.getDeputyStaff() != null){
            return getByMinorId(deputyStaffsService.getDeputyStaff().getId());
        }
        else{
            return getByMinorId(minorLecturersService.getMinorLecturer().getId());
        }
    }

    private final MajorLecturersService majorLecturersService;
    private final StaffsService staffsService;
    private final MinorLecturersService minorLecturersService;
    private final DeputyStaffsService deputyStaffsService;

    public EmployesDAOImpl(MajorLecturersService majorLecturersService, StaffsService staffsService, MinorLecturersService minorLecturersService, DeputyStaffsService deputyStaffsService) {
        this.majorLecturersService = majorLecturersService;
        this.staffsService = staffsService;
        this.minorLecturersService = minorLecturersService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @Override
    public MajorEmployes getMajorEmployee() {
        if(majorLecturersService.getMajorLecturer() != null){
            return getById(majorLecturersService.getMajorLecturer().getId());
        }
        else{
            return getById(staffsService.getStaff().getId());
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MajorEmployes getById(String id) {
        return entityManager.find(MajorEmployes.class, id);
    }

    @Override
    public List<Rooms> getAll() {
        return List.of();
    }

}
