package com.example.demo.post.specializedAssignmentSubmitSlots.dao;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
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
public class SpecializedAssignmentSubmitSlotsDAOImpl implements SpecializedAssignmentSubmitSlotsDAO {

    @Override
    public List<String> getNotificationsForMemberId(String memberId) {
        // Query 1: Student in Specialized Class
        String jpqlStudent = """
        SELECT CONCAT('New assignment in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... (Deadline: ', p.deadline, ') on ', p.createdAt)
        FROM SpecializedAssignmentSubmitSlots p
        JOIN p.classEntity c
        JOIN Students_SpecializedClasses smc ON smc.specializedClass.classId = c.classId
        JOIN smc.student s
        WHERE s.id = :memberId
          AND p.notificationType = 'SPECIALIZED_ASSIGNMENT_SLOT_CREATED'
        """;

        // Query 2: Lecturer in Specialized Class
        String jpqlLecturer = """
        SELECT CONCAT('New assignment in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... (Deadline: ', p.deadline, ') on ', p.createdAt)
        FROM SpecializedAssignmentSubmitSlots p
        JOIN p.classEntity c
        JOIN MajorLecturers_SpecializedClasses lmc ON lmc.specializedClass.classId = c.classId
        JOIN lmc.lecturer l
        WHERE l.id = :memberId
          AND p.notificationType = 'SPECIALIZED_ASSIGNMENT_SLOT_CREATED'
        """;

        List<String> studentNotifs = entityManager.createQuery(jpqlStudent, String.class)
                .setParameter("memberId", memberId)
                .getResultList();

        List<String> lecturerNotifs = entityManager.createQuery(jpqlLecturer, String.class)
                .setParameter("memberId", memberId)
                .getResultList();

        return Stream.concat(studentNotifs.stream(), lecturerNotifs.stream())
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

    @Override
    public List<SpecializedAssignmentSubmitSlots> getAllSpecializedAssignmentSubmitSlotsByClass(String classId) {
        return entityManager.createQuery("from SpecializedAssignmentSubmitSlots s where s.classEntity.classId=:classId").setParameter("classId", classId).getResultList();
    }

    @Override
    public Map<String, String> validateSlot(SpecializedAssignmentSubmitSlots slot) {
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
    public List<SpecializedAssignmentSubmitSlots> getAllSpecializedAssignmentSubmitSlotsByClass(SpecializedClasses specializedClass) {
        return entityManager.createQuery("from SpecializedAssignmentSubmitSlots a where a.classEntity=:specializedClass", SpecializedAssignmentSubmitSlots.class)
                .setParameter("specializedClass", specializedClass)
                .getResultList();
    }

    @Override
    public void save(SpecializedAssignmentSubmitSlots slot) {
        if (slot.getPostId() == null) {
            entityManager.persist(slot);
        } else {
            entityManager.merge(slot);
        }
    }

    @Override
    public SpecializedAssignmentSubmitSlots findByPostId(String postId) {
        try {
            return entityManager.createQuery("from SpecializedAssignmentSubmitSlots a where a.postId=:postId", SpecializedAssignmentSubmitSlots.class)
                    .setParameter("postId", postId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean existsByPostId(String postId) {
        Long count = entityManager.createQuery("select count(a) from SpecializedAssignmentSubmitSlots a where a.postId=:postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate date) {
        String dateStr = date.toString().replace("-", "");
        String prefix = "SASS_" + classId + "_" + dateStr + "_";
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
        SpecializedAssignmentSubmitSlots slot = findByPostId(postId);
        if (slot != null) {
            entityManager.remove(slot);
        }
    }
}