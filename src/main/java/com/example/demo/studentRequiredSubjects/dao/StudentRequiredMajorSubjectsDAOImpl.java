package com.example.demo.studentRequiredSubjects.dao;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.student.model.Students;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.entity.Enums.SubjectTypes;
import com.example.demo.entity.Enums.UpgradeStatus;
import com.example.demo.entity.UpgradeStudents;
import com.example.demo.majorstaff.service.StaffsService;
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
        // Kiểm tra subject không null, major của staff không null, và môn học đã được chấp nhận
        return subjects != null &&
                staffsService.getStaffMajor() != null &&
                subjects.getAcceptor() != null;
    }

    private boolean hasPassedAllMajorPreparationSubjects(Students student) {
        // Truy vấn kiểm tra xem sinh viên đã pass tất cả các môn MAJOR_PREPARATION
        List<Object[]> results = entityManager.createQuery(
                        "SELECT s.subjectId, t.grade FROM MajorAcademicTranscripts t " +
                                "JOIN MajorSubjects s ON t.subject.subjectId = s.subjectId " +
                                "WHERE t.student.id = :studentId AND s.requirementType = :requirementType",
                        Object[].class)
                .setParameter("studentId", student.getId())
                .setParameter("requirementType", SubjectTypes.MAJOR_PREPARATION)
                .getResultList();

        // Nếu không có môn MAJOR_PREPARATION nào, coi như pass
        if (results.isEmpty()) {
            return true;
        }

        // Kiểm tra tất cả các môn MAJOR_PREPARATION phải có grade != REFER
        return results.stream().allMatch(result -> {
            Grades grade = (Grades) result[1];
            return grade == Grades.PASS || grade == Grades.MERIT || grade == Grades.DISTINCTION;
        });
    }

    private boolean isTuitionValidForSubject(MajorSubjects subject, Students student) {
        // Kiểm tra xem môn học có học phí hợp lệ trong TuitionByYear
        List<Double> tuition = entityManager.createQuery(
                        "SELECT t.tuition FROM TuitionByYear t " +
                                "WHERE t.subject.subjectId = :subjectId AND t.id.admissionYear = :admissionYear",
                        Double.class)
                .setParameter("subjectId", subject.getSubjectId())
                .setParameter("admissionYear", student.getAdmissionYear().getYear())
                .getResultList();

        // Nếu có bản ghi trong TuitionByYear và tuition > 0, coi như hợp lệ
        return !tuition.isEmpty() && tuition.get(0) > 0;
    }

    private boolean hasCompletedEnglishStage(Students student) {
        // Kiểm tra xem sinh viên có trạng thái COMPLETE_ENGLISH_STAGE trong UpgradeStudents
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

        // Lấy danh sách sinh viên đã đăng ký môn học
        List<StudentRequiredMajorSubjects> results = entityManager.createQuery(
                        "SELECT srs FROM StudentRequiredMajorSubjects srs " +
                                "WHERE srs.subject = :subjects AND srs.student.major = :major",
                        StudentRequiredMajorSubjects.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();

        // Lọc những sinh viên đã pass MAJOR_PREPARATION, có học phí hợp lệ, và có trạng thái COMPLETE_ENGLISH_STAGE
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

        // Lấy danh sách sinh viên chưa đăng ký môn học
        List<Students> students = entityManager.createQuery(
                        "SELECT s FROM Students s LEFT JOIN StudentRequiredMajorSubjects srs " +
                                "ON s.id = srs.student.id AND srs.subject = :subjects " +
                                "WHERE s.major = :major AND srs.student.id IS NULL",
                        Students.class)
                .setParameter("subjects", subjects)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();

        // Lọc những sinh viên đã pass MAJOR_PREPARATION, có học phí hợp lệ, và có trạng thái COMPLETE_ENGLISH_STAGE
        return students.stream()
                .filter(student -> hasPassedAllMajorPreparationSubjects(student) &&
                        isTuitionValidForSubject(subjects, student) &&
                        hasCompletedEnglishStage(student))
                .toList();
    }
}