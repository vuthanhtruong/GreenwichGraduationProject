package com.example.demo.post.majorAssignmentSubmitSlots.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
@Transactional
public class AssignmentSubmitSlotsDAOImpl implements AssignmentSubmitSlotsDAO {

    @Override
    public List<String> getNotificationsForMemberId(String memberId) {
        // Query 1: Student in Major Class
        String jpqlStudent = """
        SELECT CONCAT('New assignment in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... (Deadline: ', p.deadline, ') on ', p.createdAt)
        FROM AssignmentSubmitSlots p
        JOIN p.classEntity c
        JOIN Students_MajorClasses smc ON smc.majorClass.classId = c.classId
        JOIN smc.student s
        WHERE s.id = :memberId
          AND p.notificationType = 'MAJOR_ASSIGNMENT_SLOT_CREATED'
        """;

        // Query 2: Lecturer in Major Class
        String jpqlLecturer = """
        SELECT CONCAT('New assignment in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... (Deadline: ', p.deadline, ') on ', p.createdAt)
        FROM AssignmentSubmitSlots p
        JOIN p.classEntity c
        JOIN MajorLecturers_MajorClasses lmc ON lmc.majorClass.classId = c.classId
        JOIN lmc.lecturer l
        WHERE l.id = :memberId
          AND p.notificationType = 'MAJOR_ASSIGNMENT_SLOT_CREATED'
        """;

        List<String> studentNotifs = entityManager.createQuery(jpqlStudent, String.class)
                .setParameter("memberId", memberId)
                .getResultList();

        List<String> lecturerNotifs = entityManager.createQuery(jpqlLecturer, String.class)
                .setParameter("memberId", memberId)
                .getResultList();

        // === GỘP + SẮP XẾP THEO THỜI GIAN MỚI NHẤT ===
        return Stream.concat(studentNotifs.stream(), lecturerNotifs.stream())
                .distinct()
                .sorted((a, b) -> {
                    try {
                        String timeA = a.substring(a.lastIndexOf(" on ") + 4).trim();
                        String timeB = b.substring(b.lastIndexOf(" on ") + 4).trim();
                        LocalDateTime dtA = LocalDateTime.parse(timeA);
                        LocalDateTime dtB = LocalDateTime.parse(timeB);
                        return dtB.compareTo(dtA); // mới nhất trước
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .toList();
    }

    @Override
    public List<AssignmentSubmitSlots> getAssignmentSubmitSlotsByClass(String majorClass) {
        return entityManager.createQuery("from AssignmentSubmitSlots a where a.classEntity.classId=:majorClass").setParameter("majorClass", majorClass).getResultList();
    }

    @Override
    public Map<String, String> validateSlot(AssignmentSubmitSlots slot) {
        Map<String, String> errors = new HashMap<>();
        if (slot.getContent() != null && slot.getContent().length() > 1000) {
            errors.put("content", "Content cannot exceed 1000 characters");
        }
        if (slot.getDeadline() == null) {
            errors.put("deadline", "Deadline is required");
        } else if (slot.getDeadline().isBefore(LocalDateTime.now())) {
            errors.put("deadline", "Deadline must be in the future");
        }
        if (slot.getClassEntity() == null) {
            errors.put("class", "Class is required");
        }
        if (slot.getCreator() == null) {
            errors.put("creator", "Creator is required");
        }
        return errors;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AssignmentSubmitSlots> getAllAssignmentSubmitSlotsByClass(MajorClasses majorClass) {
        return entityManager.createQuery("from AssignmentSubmitSlots a where a.classEntity=:majorClass", AssignmentSubmitSlots.class)
                .setParameter("majorClass", majorClass)
                .getResultList();
    }

    @Override
    public void save(AssignmentSubmitSlots slot) {
        if (slot.getPostId() == null) {
            entityManager.persist(slot);
        } else {
            entityManager.merge(slot);
        }
    }

    @Override
    public AssignmentSubmitSlots findByPostId(String postId) {
        try {
            return entityManager.createQuery("from AssignmentSubmitSlots a where a.postId=:postId", AssignmentSubmitSlots.class)
                    .setParameter("postId", postId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean existsByPostId(String postId) {
        Long count = entityManager.createQuery("select count(a) from AssignmentSubmitSlots a where a.postId=:postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate date) {
        String dateStr = date.toString().replace("-", "");
        String prefix = "ASS_" + classId + "_" + dateStr + "_";
        int count = 1;
        String postId;
        do {
            postId = prefix + String.format("%03d", count);
            count++;
        } while (entityManager.find(ClassPosts.class, postId) != null);
        return postId;
    }

    @Override
    public void deleteByPostId(String postId) {
        AssignmentSubmitSlots slot = findByPostId(postId);
        if (slot != null) {
            entityManager.remove(slot);
        }
    }
}