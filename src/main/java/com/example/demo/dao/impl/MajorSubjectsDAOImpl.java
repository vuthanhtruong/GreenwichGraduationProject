package com.example.demo.dao.impl;

import com.example.demo.dao.MajorSubjectsDAO;
import com.example.demo.entity.AbstractClasses.Subjects;
import com.example.demo.entity.Enums.SubjectTypes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SyllabusesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class MajorSubjectsDAOImpl implements MajorSubjectsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;
    private final SyllabusesService syllabusesService;
    private final ClassesService classesService;

    public MajorSubjectsDAOImpl(StaffsService staffsService, SyllabusesService syllabusesService, ClassesService classesService) {
        if (staffsService == null || syllabusesService == null || classesService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.staffsService = staffsService;
        this.syllabusesService = syllabusesService;
        this.classesService = classesService;
    }

    @Override
    public boolean existsBySubjectExcludingName(String SubjectName, String SubjectId) {
        if (SubjectName == null || SubjectName.trim().isEmpty()) {
            return false;
        }
        List<Subjects> Subjects = entityManager.createQuery(
                        "SELECT s FROM Subjects s WHERE s.subjectName = :name AND s.subjectId != :SubjectId", Subjects.class)
                .setParameter("name", SubjectName.trim())
                .setParameter("SubjectId", SubjectId != null ? SubjectId : "")
                .getResultList();
        return !Subjects.isEmpty();
    }

    @Override
    public void addSubject(MajorSubjects subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject object cannot be null");
        }
        if (staffsService.getStaff() == null || staffsService.getStaffMajor() == null) {
            throw new IllegalArgumentException("Staff or major not found");
        }
        List<String> errors = validateSubject(subject);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
        subject.setCreator(staffsService.getStaff());
        subject.setMajor(staffsService.getStaffMajor());
        subject.setRequirementType(SubjectTypes.TOPUP_PREPARATION);
        entityManager.persist(subject);
    }

    @Override
    public MajorSubjects getSubjectById(String subjectId) {
        if (subjectId == null) {
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        return entityManager.find(MajorSubjects.class, subjectId);
    }

    @Override
    public MajorSubjects getSubjectByName(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return null;
        }
        List<MajorSubjects> subjects = entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.subjectName = :name", MajorSubjects.class)
                .setParameter("name", subjectName.trim())
                .getResultList();
        return subjects.isEmpty() ? null : subjects.get(0);
    }

    @Override
    public MajorSubjects checkNameSubject(MajorSubjects subject) {
        return getSubjectByName(subject != null ? subject.getSubjectName() : null);
    }

    @Override
    public List<MajorSubjects> subjectsByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.major = :major ORDER BY s.semester ASC",
                        MajorSubjects.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<MajorSubjects> AcceptedSubjectsByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.major = :major AND s.acceptor.id IS NOT NULL ORDER BY s.semester ASC",
                        MajorSubjects.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<MajorSubjects> getSubjects() {
        return entityManager.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList();
    }

    @Override
    public MajorSubjects updateSubject(String id, MajorSubjects subject) {
        if (subject == null || id == null) {
            throw new IllegalArgumentException("Subject object or ID cannot be null");
        }

        MajorSubjects existingSubject = entityManager.find(MajorSubjects.class, id);
        if (existingSubject == null) {
            throw new IllegalArgumentException("Subject with ID " + id + " not found");
        }

        List<String> errors = validateSubject(subject);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        existingSubject.setSubjectName(subject.getSubjectName());
        if (subject.getSemester() != null) existingSubject.setSemester(subject.getSemester());

        return entityManager.merge(existingSubject);
    }

    @Override
    public void deleteSubject(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        MajorSubjects subject = entityManager.find(MajorSubjects.class, id);
        if (subject != null) {
            syllabusesService.deleteSyllabusBySubject(subject);
            classesService.SetNullWhenDeletingSubject(subject);
            entityManager.remove(subject);
        }
    }

    @Override
    public String generateUniqueSubjectId(String majorId, LocalDate createdDate) {
        String prefix;
        switch (majorId) {
            case "major001":
                prefix = "SUBGBH";
                break;
            case "major002":
                prefix = "SUBGCH";
                break;
            case "major003":
                prefix = "SUBGDH";
                break;
            case "major004":
                prefix = "SUBGKH";
                break;
            default:
                prefix = "SUBGEN";
                break;
        }

        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());

        String subjectId;
        SecureRandom random = new SecureRandom();
        do {
            String randomDigit = String.valueOf(random.nextInt(10));
            subjectId = prefix + year + date + randomDigit;
        } while (getSubjectById(subjectId) != null);
        return subjectId;
    }

    @Override
    public List<String> validateSubject(MajorSubjects subject) {
        List<String> errors = new ArrayList<>();

        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            errors.add("Subject name cannot be blank.");
        } else if (!isValidName(subject.getSubjectName())) {
            errors.add("Subject name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        MajorSubjects existingSubjectByName = getSubjectByName(subject.getSubjectName());
        if (subject.getSubjectName() != null && existsBySubjectExcludingName(subject.getSubjectName(), subject.getSubjectId())) {
            errors.add("Subject name is already in use.");
        }

        return errors;
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}