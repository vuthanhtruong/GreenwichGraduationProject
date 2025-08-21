package com.example.demo.lecturer_class.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.entity.LecturersClassesId; // Corrected from MajorLecturersClassesId
import com.example.demo.lecturer_class.model.MajorLecturers_MajorClasses;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.majorStaff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class MajorLecturers_MajorClassesDAOImpl implements Lecturers_ClassesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final PersonsService personsService;
    private final LecturesService lecturersService; // Corrected service name
    private final StaffsService staffsService;

    public MajorLecturers_MajorClassesDAOImpl(PersonsService personsService, LecturesService lecturersService, StaffsService staffsService) {
        this.personsService = personsService;
        this.lecturersService = lecturersService;
        this.staffsService = staffsService;
    }

    @Override
    public void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds) {
        if (classes == null || lecturerIds == null || lecturerIds.isEmpty()) {
            return; // No action if inputs are invalid
        }

        for (String lecturerId : lecturerIds) {
            // Find the Lecturers_Classes record by composite key
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            MajorLecturers_MajorClasses lecturerClass = entityManager.find(MajorLecturers_MajorClasses.class, id);
            if (lecturerClass != null) {
                entityManager.remove(lecturerClass); // Delete the record
            }
        }
    }

    @Override
    public void addLecturersToClass(MajorClasses classes, List<String> lecturerIds) {
        for (String lecturerId : lecturerIds) {
            MajorLecturers lecturer = lecturersService.getLecturerById(lecturerId);
            MajorLecturers_MajorClasses lecturerClass = new MajorLecturers_MajorClasses();
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            lecturerClass.setId(id);
            lecturerClass.setClassEntity(classes);
            lecturerClass.setMajorLecturer(lecturer); // Corrected from setLecturer to setMajorLecturer
            lecturerClass.setCreatedAt(LocalDateTime.now());
            lecturerClass.setAddedBy(staffsService.getStaff()); // Assuming getStaff returns a Staffs entity
            entityManager.persist(lecturerClass);
        }
    }

    @Override
    public List<MajorLecturers_MajorClasses> listLecturersInClass(MajorClasses classes) {
        return entityManager.createQuery(
                        "SELECT lc FROM MajorLecturers_MajorClasses lc WHERE lc.classEntity = :class AND lc.majorLecturer.majorManagement = :major",
                        MajorLecturers_MajorClasses.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor()) // Assuming getStaffMajor returns a Major entity
                .getResultList();
    }

    @Override
    public List<MajorLecturers> listLecturersNotInClass(MajorClasses classes) {
        return entityManager.createQuery(
                        "SELECT l FROM MajorLecturers l WHERE l.majorManagement = :major AND l.id NOT IN " +
                                "(SELECT lc.majorLecturer.id FROM MajorLecturers_MajorClasses lc WHERE lc.classEntity = :class)",
                        MajorLecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor()) // Assuming getStaffMajor returns a Major entity
                .getResultList();
    }
}