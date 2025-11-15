package com.example.demo.academicTranscript.dao;

import com.example.demo.academicTranscript.model.*;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class AcademicTranscriptsDAOImpl implements AcademicTranscriptsDAO {

    @Override
    public List<String> getNotificationsForMemberId(String memberId) {
        List<String> allNotifications = new ArrayList<>();

        // === MAJOR GRADES ===
        String jpqlMajorStudent = """
        SELECT CONCAT('Your major subject grade has been updated to ', 
                      t.grade, 
                      ' in ', 
                      COALESCE(c.subject.subjectName, c.nameClass), 
                      ' by ', 
                      COALESCE(staff.firstName, 'Staff'), ' ', COALESCE(staff.lastName, ''), 
                      ' on ', t.createdAt)
        FROM MajorAcademicTranscripts t
        JOIN t.student s
        JOIN t.majorClass c
        LEFT JOIN c.subject subj
        LEFT JOIN t.creator staff
        WHERE s.id = :memberId
          AND t.grade IS NOT NULL
        """;

        String jpqlMajorStaff = """
        SELECT CONCAT('You updated grade for ', 
                      stu.firstName, ' ', stu.lastName, 
                      ' to ', t.grade, 
                      ' in ', COALESCE(c.subject.subjectName, c.nameClass), 
                      ' on ', t.createdAt)
        FROM MajorAcademicTranscripts t
        JOIN t.creator staff
        JOIN t.student stu
        JOIN t.majorClass c
        LEFT JOIN c.subject subj
        WHERE staff.id = :memberId
          AND t.grade IS NOT NULL
        """;

        // === MINOR GRADES ===
        String jpqlMinorStudent = """
        SELECT CONCAT('Your minor subject grade has been updated to ', 
                      t.grade, 
                      ' in ', 
                      COALESCE(c.minorSubject.subjectName, c.nameClass), 
                      ' by ', 
                      COALESCE(staff.firstName, 'Deputy'), ' ', COALESCE(staff.lastName, ''), 
                      ' on ', t.createdAt)
        FROM MinorAcademicTranscripts t
        JOIN t.student s
        JOIN t.minorClass c
        LEFT JOIN c.minorSubject subj
        LEFT JOIN t.creator staff
        WHERE s.id = :memberId
          AND t.grade IS NOT NULL
        """;

        String jpqlMinorStaff = """
        SELECT CONCAT('You updated grade for ', 
                      stu.firstName, ' ', stu.lastName, 
                      ' to ', t.grade, 
                      ' in ', COALESCE(c.minorSubject.subjectName, c.nameClass), 
                      ' on ', t.createdAt)
        FROM MinorAcademicTranscripts t
        JOIN t.creator staff
        JOIN t.student stu
        JOIN t.minorClass c
        LEFT JOIN c.minorSubject subj
        WHERE staff.id = :memberId
          AND t.grade IS NOT NULL
        """;

        // === SPECIALIZED GRADES ===
        String jpqlSpecStudent = """
        SELECT CONCAT('Your specialized subject grade has been updated to ', 
                      t.grade, 
                      ' in ', 
                      COALESCE(c.specializedSubject.subjectName, c.nameClass), 
                      ' by ', 
                      COALESCE(staff.firstName, 'Staff'), ' ', COALESCE(staff.lastName, ''), 
                      ' on ', t.createdAt)
        FROM SpecializedAcademicTranscripts t
        JOIN t.student s
        JOIN t.specializedClass c
        LEFT JOIN c.specializedSubject subj
        LEFT JOIN t.creator staff
        WHERE s.id = :memberId
          AND t.grade IS NOT NULL
        """;

        String jpqlSpecStaff = """
        SELECT CONCAT('You updated grade for ', 
                      stu.firstName, ' ', stu.lastName, 
                      ' to ', t.grade, 
                      ' in ', COALESCE(c.specializedSubject.subjectName, c.nameClass), 
                      ' on ', t.createdAt)
        FROM SpecializedAcademicTranscripts t
        JOIN t.creator staff
        JOIN t.student stu
        JOIN t.specializedClass c
        LEFT JOIN c.specializedSubject subj
        WHERE staff.id = :memberId
          AND t.grade IS NOT NULL
        """;

        // === THỰC THI 6 TRUY VẤN ===
        allNotifications.addAll(entityManager.createQuery(jpqlMajorStudent, String.class)
                .setParameter("memberId", memberId).getResultList());
        allNotifications.addAll(entityManager.createQuery(jpqlMajorStaff, String.class)
                .setParameter("memberId", memberId).getResultList());
        allNotifications.addAll(entityManager.createQuery(jpqlMinorStudent, String.class)
                .setParameter("memberId", memberId).getResultList());
        allNotifications.addAll(entityManager.createQuery(jpqlMinorStaff, String.class)
                .setParameter("memberId", memberId).getResultList());
        allNotifications.addAll(entityManager.createQuery(jpqlSpecStudent, String.class)
                .setParameter("memberId", memberId).getResultList());
        allNotifications.addAll(entityManager.createQuery(jpqlSpecStaff, String.class)
                .setParameter("memberId", memberId).getResultList());

        // === SẮP XẾP THEO createdAt MỚI NHẤT TRƯỚC ===
        return allNotifications.stream()
                .distinct()
                .sorted((a, b) -> {
                    try {
                        String timeA = a.substring(a.lastIndexOf(" on ") + 4).trim();
                        String timeB = b.substring(b.lastIndexOf(" on ") + 4).trim();
                        LocalDateTime dtA = LocalDateTime.parse(timeA);
                        LocalDateTime dtB = LocalDateTime.parse(timeB);
                        return dtB.compareTo(dtA);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .toList();
    }

    private static final Logger log = LoggerFactory.getLogger(AcademicTranscriptsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MajorAcademicTranscripts> getTranscriptsByClass(MajorClasses majorClass) {
        try {
            return entityManager.createQuery(
                            "SELECT t FROM MajorAcademicTranscripts t WHERE t.majorClass = :majorClass",
                            MajorAcademicTranscripts.class)
                    .setParameter("majorClass", majorClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching Major transcripts for class ID: {}", majorClass.getClassId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public MajorAcademicTranscripts findOrCreateTranscript(String transcriptId,
                                                           Students student,
                                                           MajorClasses majorClass,
                                                           Staffs creator) {
        MajorAcademicTranscripts t = entityManager.find(MajorAcademicTranscripts.class, transcriptId);
        if (t == null) {
            t = new MajorAcademicTranscripts();
            t.setTranscriptId(transcriptId);
            t.setStudent(student);
            t.setMajorClass(majorClass);
            t.setCreator(creator);
            t.setCreatedAt(LocalDateTime.now());
            log.debug("Created new Major transcript ID: {}", transcriptId);
        }
        return t;
    }

    @Override
    public void saveOrUpdateTranscript(MajorAcademicTranscripts transcript) {
        try {
            if (transcript.getTranscriptId() == null) {
                entityManager.persist(transcript);
                log.debug("Persisted new Major transcript ID: {}", transcript.getTranscriptId());
            } else {
                entityManager.merge(transcript);
                log.debug("Merged Major transcript ID: {}", transcript.getTranscriptId());
            }
        } catch (Exception e) {
            log.error("Error saving Major transcript ID: {}", transcript.getTranscriptId(), e);
        }
    }

    @Override
    public List<SpecializedAcademicTranscripts> getTranscriptsByClass(SpecializedClasses specializedClass) {
        try {
            return entityManager.createQuery(
                            "SELECT t FROM SpecializedAcademicTranscripts t WHERE t.specializedClass = :specializedClass",
                            SpecializedAcademicTranscripts.class)
                    .setParameter("specializedClass", specializedClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching Specialized transcripts for class ID: {}", specializedClass.getClassId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public SpecializedAcademicTranscripts findOrCreateTranscript(String transcriptId,
                                                                 Students student,
                                                                 SpecializedClasses specializedClass,
                                                                 Staffs creator) {
        SpecializedAcademicTranscripts t = entityManager.find(SpecializedAcademicTranscripts.class, transcriptId);
        if (t == null) {
            t = new SpecializedAcademicTranscripts();
            t.setTranscriptId(transcriptId);
            t.setStudent(student);
            t.setSpecializedClass(specializedClass);
            t.setCreator(creator);
            t.setCreatedAt(LocalDateTime.now());
            log.debug("Created new Specialized transcript ID: {}", transcriptId);
        }
        return t;
    }

    @Override
    public void saveOrUpdateTranscript(SpecializedAcademicTranscripts transcript) {
        try {
            if (transcript.getTranscriptId() == null) {
                entityManager.persist(transcript);
                log.debug("Persisted new Specialized transcript ID: {}", transcript.getTranscriptId());
            } else {
                entityManager.merge(transcript);
                log.debug("Merged Specialized transcript ID: {}", transcript.getTranscriptId());
            }
        } catch (Exception e) {
            log.error("Error saving Specialized transcript ID: {}", transcript.getTranscriptId(), e);
        }
    }

    @Override
    public List<MinorAcademicTranscripts> getTranscriptsByClass(MinorClasses minorClass) {
        try {
            return entityManager.createQuery(
                            "SELECT t FROM MinorAcademicTranscripts t WHERE t.minorClass = :minorClass",
                            MinorAcademicTranscripts.class)
                    .setParameter("minorClass", minorClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching Minor transcripts for class ID: {}", minorClass.getClassId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public MinorAcademicTranscripts findOrCreateTranscript(String transcriptId,
                                                           Students student,
                                                           MinorClasses minorClass,
                                                           DeputyStaffs creator) {
        MinorAcademicTranscripts t = entityManager.find(MinorAcademicTranscripts.class, transcriptId);
        if (t == null) {
            t = new MinorAcademicTranscripts();
            t.setTranscriptId(transcriptId);
            t.setStudent(student);
            t.setMinorClass(minorClass);
            t.setCreator(creator);
            t.setCreatedAt(LocalDateTime.now());
            log.debug("Created new Minor transcript ID: {}", transcriptId);
        }
        return t;
    }

    @Override
    public void saveOrUpdateTranscript(MinorAcademicTranscripts transcript) {
        try {
            if (transcript.getTranscriptId() == null) {
                entityManager.persist(transcript);
            } else {
                entityManager.merge(transcript);
            }
        } catch (Exception e) {
            log.error("Error saving Minor transcript ID: {}", transcript.getTranscriptId(), e);
        }
    }

    @Override
    public List<AcademicTranscripts> getFailSubjectsByStudent(Students student) {
        try {
            return entityManager.createQuery(
                            "SELECT a FROM AcademicTranscripts a " +
                                    "WHERE a.student = :student AND a.grade = :failGrade",
                            AcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("failGrade", Grades.REFER)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching failed subjects for student ID: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MajorAcademicTranscripts> getAcademicTranscriptsByMajorClass(Students student, MajorClasses majorClass) {
        try {
            return entityManager.createQuery(
                            "FROM MajorAcademicTranscripts m WHERE m.student = :student AND m.majorClass = :majorClass",
                            MajorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("majorClass", majorClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching Major transcript by class for student: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MinorAcademicTranscripts> getAcademicTranscriptsByMinorClass(Students student, MinorClasses minorClass) {
        try {
            return entityManager.createQuery(
                            "FROM MinorAcademicTranscripts m WHERE m.student = :student AND m.minorClass = :minorClass",
                            MinorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("minorClass", minorClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching Minor transcript by class for student: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SpecializedAcademicTranscripts> getAcademicTranscriptsBySpecializedClass(Students student, SpecializedClasses specializedClass) {
        try {
            return entityManager.createQuery(
                            "FROM SpecializedAcademicTranscripts s WHERE s.student = :student AND s.specializedClass = :specializedClass",
                            SpecializedAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("specializedClass", specializedClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching Specialized transcript by class for student: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Students_Classes> getLearningProcess(Students student) {
        try {
            return entityManager.createQuery(
                            "FROM Students_Classes sc WHERE sc.student = :student",
                            Students_Classes.class)
                    .setParameter("student", student)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching learning process for student ID: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MajorAcademicTranscripts> getMajorAcademicTranscripts(Students student) {
        if (student == null) {
            log.debug("Received null student for MajorAcademicTranscripts query");
            return new ArrayList<>();
        }
        try {
            List<MajorAcademicTranscripts> transcripts = entityManager
                    .createQuery(
                            "SELECT m FROM MajorAcademicTranscripts m " +
                                    "WHERE m.student = :student AND m.grade != :refer",
                            MajorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("refer", Grades.REFER)
                    .getResultList();
            return transcripts != null ? transcripts : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching MajorAcademicTranscripts for student ID: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student) {
        if (student == null) {
            log.debug("Received null student for MinorAcademicTranscripts query");
            return new ArrayList<>();
        }
        try {
            List<MinorAcademicTranscripts> transcripts = entityManager
                    .createQuery(
                            "SELECT m FROM MinorAcademicTranscripts m " +
                                    "WHERE m.student = :student AND m.grade != :refer",
                            MinorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("refer", Grades.REFER)
                    .getResultList();
            return transcripts != null ? transcripts : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching MinorAcademicTranscripts for student ID: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SpecializedAcademicTranscripts> getSpecializedAcademicTranscripts(Students student) {
        if (student == null) {
            log.debug("Received null student for SpecializedAcademicTranscripts query");
            return new ArrayList<>();
        }
        try {
            List<SpecializedAcademicTranscripts> transcripts = entityManager
                    .createQuery(
                            "SELECT s FROM SpecializedAcademicTranscripts s " +
                                    "WHERE s.student = :student AND s.grade != :refer",
                            SpecializedAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("refer", Grades.REFER)
                    .getResultList();
            return transcripts != null ? transcripts : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching SpecializedAcademicTranscripts for student ID: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }
    // === THÊM 2 HÀM MỚI ===
    @Override
    public List<Students> getStudentsWithScoresByMajorClass(MajorClasses majorClass) {
        try {
            return entityManager.createQuery(
                            "SELECT DISTINCT t.student FROM MajorAcademicTranscripts t WHERE t.majorClass = :majorClass",
                            Students.class)
                    .setParameter("majorClass", majorClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching students with scores for Major class ID: {}", majorClass.getClassId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Students> getStudentsWithoutScoresByMajorClass(MajorClasses majorClass) {
        try {
            return entityManager.createQuery(
                            "SELECT s FROM Students_MajorClasses sc " +
                                    "JOIN sc.student s " +
                                    "WHERE sc.majorClass = :majorClass " +
                                    "AND s.id NOT IN (" +
                                    "  SELECT t.student.id FROM MajorAcademicTranscripts t WHERE t.majorClass = :majorClass" +
                                    ")",
                            Students.class)
                    .setParameter("majorClass", majorClass)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching students without scores for Major class ID: {}", majorClass.getClassId(), e);
            return new ArrayList<>();
        }
    }
}