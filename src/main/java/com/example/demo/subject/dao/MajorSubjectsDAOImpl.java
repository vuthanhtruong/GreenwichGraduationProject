package com.example.demo.subject.dao;

import com.example.demo.entity.Enums.SubjectTypes;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.subject.model.Subjects;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.syllabus.service.SyllabusesService;
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
    public boolean existsBySubjectExcludingName(String subjectName, String subjectId) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return false;
        }
        List<Subjects> subjects = entityManager.createQuery(
                        "SELECT s FROM Subjects s WHERE s.subjectName = :name AND s.subjectId != :subjectId", Subjects.class)
                .setParameter("name", subjectName.trim())
                .setParameter("subjectId", subjectId != null ? subjectId : "")
                .getResultList();
        return !subjects.isEmpty();
    }

    @Override
    public void addSubject(MajorSubjects subject) {
        subject.setCreator(staffsService.getStaff());
        subject.setMajor(staffsService.getStaffMajor());
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
                        "SELECT s FROM MajorSubjects s WHERE s.major = :major ORDER BY s.semester ASC",
                        MajorSubjects.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<MajorSubjects> getSubjects() {
        return entityManager.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class)
                .getResultList();
    }

    @Override
    public MajorSubjects editSubject(String id, MajorSubjects subject) {
        MajorSubjects existingSubject = entityManager.find(MajorSubjects.class, id);
        if(subject.getSubjectName() != null) {
            existingSubject.setSubjectName(subject.getSubjectName());
        }
        if (subject.getSemester() != null){
            existingSubject.setSemester(subject.getSemester());
        }
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
        if (existsBySubjectExcludingName(subject.getSubjectName(), subject.getSubjectId())) {
            errors.add("Subject name is already in use.");
        }
        if (subject.getSemester() == null || subject.getSemester() < 1) {
            errors.add("Semester must be a positive number.");
        }
        return errors;
    }

    @Override
    public List<MajorSubjects> getPaginatedSubjects(int firstResult, int pageSize, Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.major = :major",
                        MajorSubjects.class)
                .setParameter("major", major)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfSubjects(Majors major) {
        if (major == null) {
            return 0;
        }
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM MajorSubjects s WHERE s.major = :major",
                        Long.class)
                .setParameter("major", major)
                .getSingleResult();
    }

    @Override
    public List<MajorSubjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        if (major == null || keyword == null || searchType == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        String queryString = "SELECT ms FROM MajorSubjects ms JOIN Subjects s ON ms.subjectId = s.subjectId WHERE ms.major = :major";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(s.subjectName) LIKE LOWER(:keyword)";
        } else if ("id".equals(searchType)) {
            queryString += " AND s.subjectId LIKE :keyword";
        } else {
            return List.of();
        }
        return entityManager.createQuery(queryString, MajorSubjects.class)
                .setParameter("major", major)
                .setParameter("keyword", "%" + keyword.trim() + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        if (major == null || keyword == null || searchType == null || keyword.trim().isEmpty()) {
            return 0;
        }
        String queryString = "SELECT COUNT(ms) FROM MajorSubjects ms JOIN Subjects s ON ms.subjectId = s.subjectId WHERE ms.major = :major";
        if ("name".equals(searchType)) {
            queryString += " AND LOWER(s.subjectName) LIKE LOWER(:keyword)";
        } else if ("id".equals(searchType)) {
            queryString += " AND s.subjectId LIKE :keyword";
        } else {
            return 0;
        }
        return entityManager.createQuery(queryString, Long.class)
                .setParameter("major", major)
                .setParameter("keyword", "%" + keyword.trim() + "%")
                .getSingleResult();
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}0-9][\\p{L}0-9 .'-]{0,49}$";
        return name.matches(nameRegex);
    }
}