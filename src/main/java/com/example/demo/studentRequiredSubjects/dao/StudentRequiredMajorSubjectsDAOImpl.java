package com.example.demo.studentRequiredSubjects.dao;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.student.model.Students;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.entity.Enums.SubjectTypes;
import com.example.demo.entity.Enums.UpgradeStatus;
import com.example.demo.entity.UpgradeStudents;
import com.example.demo.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class StudentRequiredMajorSubjectsDAOImpl implements StudentRequiredMajorSubjectsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public StudentRequiredMajorSubjectsDAOImpl(StaffsService staffsService) {
        if (staffsService == null) {
            throw new IllegalArgumentException("StaffsService cannot be null");
        }
        this.staffsService = staffsService;
    }

    private boolean isValidSubjectAndMajor(MajorSubjects subjects) {
        return subjects != null &&
                staffsService.getStaffMajor() != null &&
                subjects.getAcceptor() != null;
    }

    private boolean hasPassedAllMajorPreparationSubjects(Students student) {
        List<Object[]> results = entityManager.createQuery(
                        "SELECT s.subjectId, t.grade FROM MajorAcademicTranscripts t " +
                                "JOIN MajorSubjects s ON t.subject.subjectId = s.subjectId " +
                                "WHERE t.student.id = :studentId AND s.requirementType = :requirementType",
                        Object[].class)
                .setParameter("studentId", student.getId())
                .setParameter("requirementType", SubjectTypes.MAJOR_PREPARATION)
                .getResultList();

        if (results.isEmpty()) {
            return true;
        }

        return results.stream().allMatch(result -> {
            Grades grade = (Grades) result[1];
            return grade == Grades.PASS || grade == Grades.MERIT || grade == Grades.DISTINCTION;
        });
    }

    private boolean isTuitionValidForSubject(MajorSubjects subject, Students student) {
        List<Double> tuition = entityManager.createQuery(
                        "SELECT t.tuition FROM TuitionByYear t " +
                                "WHERE t.subject.subjectId = :subjectId AND t.id.admissionYear = :admissionYear",
                        Double.class)
                .setParameter("subjectId", subject.getSubjectId())
                .setParameter("admissionYear", student.getAdmissionYear().getYear())
                .getResultList();

        return !tuition.isEmpty() && tuition.get(0) > 0;
    }

    private boolean hasCompletedEnglishStage(Students student) {
        List<UpgradeStudents> results = entityManager.createQuery(
                        "SELECT u FROM UpgradeStudents u WHERE u.student.id = :studentId AND u.id.upgradeStatus = :status",
                        UpgradeStudents.class)
                .setParameter("studentId", student.getId())
                .setParameter("status", UpgradeStatus.COMPLETE_ENGLISH_STAGE)
                .getResultList();

        return !results.isEmpty();
    }

    @Override
    public List<StudentRequiredMajorSubjects> getStudentRequiredMajorSubjects(MajorSubjects subjects) {
        if (!isValidSubjectAndMajor(subjects)) {
            return List.of();
        }

        List<StudentRequiredMajorSubjects> results = entityManager.createQuery(
                        "SELECT srs FROM StudentRequiredMajorSubjects srs " +
                                "WHERE srs.subject = :subjects AND srs.student.major = :major",
                        StudentRequiredMajorSubjects.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();

        return results.stream()
                .filter(srs -> hasPassedAllMajorPreparationSubjects(srs.getStudent()) &&
                        isTuitionValidForSubject(subjects, srs.getStudent()) &&
                        hasCompletedEnglishStage(srs.getStudent()))
                .toList();
    }

    @Override
    public List<Students> getStudentNotRequiredMajorSubjects(MajorSubjects subjects) {
        if (!isValidSubjectAndMajor(subjects)) {
            return List.of();
        }

        List<Students> students = entityManager.createQuery(
                        "SELECT s FROM Students s LEFT JOIN StudentRequiredMajorSubjects srs " +
                                "ON s.id = srs.student.id AND srs.subject = :subjects " +
                                "WHERE s.major = :major AND srs.student.id IS NULL",
                        Students.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();

        return students.stream()
                .filter(student -> hasPassedAllMajorPreparationSubjects(student) &&
                        isTuitionValidForSubject(subjects, student) &&
                        hasCompletedEnglishStage(student))
                .toList();
    }

    @Override
    public List<MajorSubjects> getSubjectsByLearningProgramType(String learningProgramType) {
        if (learningProgramType == null || learningProgramType.trim().isEmpty()) {
            // Nếu learningProgramType rỗng, trả về tất cả môn học thuộc chuyên ngành của nhân viên
            return entityManager.createQuery(
                            "SELECT s FROM MajorSubjects s WHERE s.major = :major ORDER BY s.semester ASC",
                            MajorSubjects.class)
                    .setParameter("major", staffsService.getStaffMajor())
                    .getResultList();
        }

        // Chuyển đổi String thành enum LearningProgramTypes
        LearningProgramTypes programType;
        try {
            programType = LearningProgramTypes.valueOf(learningProgramType);
        } catch (IllegalArgumentException e) {
            // Nếu learningProgramType không hợp lệ, trả về danh sách rỗng
            return List.of();
        }

        // Trả về danh sách môn học theo learningProgramType và chuyên ngành của nhân viên
        return entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.learningProgramType = :learningProgramType AND s.major = :major ORDER BY s.semester ASC",
                        MajorSubjects.class)
                .setParameter("learningProgramType", programType)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }
}