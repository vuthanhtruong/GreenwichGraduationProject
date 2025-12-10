package com.example.demo.post.minorClassPosts.dao;

import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
@Transactional
public class MinorClassPostsDAOImpl implements MinorClassPostsDAO {

    @Override
    public void deleteMinorClassPost(String minorClassPostsId) {
        MinorClassPosts post = entityManager.find(MinorClassPosts.class, minorClassPostsId);
        if (post != null) {
            // Xóa comment liên quan trước
            entityManager.createQuery("DELETE FROM MinorComments c WHERE c.post.postId = :postId")
                    .setParameter("postId", minorClassPostsId)
                    .executeUpdate();

            // Xóa post
            entityManager.remove(post);
        }
    }



    @Override
    public List<String> getNotificationsForMemberId(String memberId) {
        // Query 1: Student in Minor Class
        String jpqlStudent = """
        SELECT CONCAT('New post in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... on ', p.createdAt)
        FROM MinorClassPosts p
        JOIN p.minorClass c
        JOIN Students_MinorClasses smc ON smc.minorClass.classId = c.classId
        JOIN smc.student s
        WHERE s.id = :memberId
          AND p.notificationType = 'MINOR_POST_CREATED'
        """;

        // Query 2: Lecturer in Minor Class
        String jpqlLecturer = """
        SELECT CONCAT('New post in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... on ', p.createdAt)
        FROM MinorClassPosts p
        JOIN p.minorClass c
        JOIN MinorLecturers_MinorClasses lmc ON lmc.minorClass.classId = c.classId
        JOIN lmc.lecturer l
        WHERE l.id = :memberId
          AND p.notificationType = 'MINOR_POST_CREATED'
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MinorClassPosts getMinorClassPost(String minorClassPostsId) {
        return entityManager.find(MinorClassPosts.class, minorClassPostsId);
    }

    @Override
    public void saveMinorClassPosts(MinorClassPosts minorClassPosts) {
        entityManager.persist(minorClassPosts);
    }

    @Override
    public List<MinorClassPosts> getClassPostByClass(String classId) {
        return entityManager.createQuery(
                        "FROM MinorClassPosts m WHERE m.minorClass.classId = :classId", MinorClassPosts.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public Map<String, String> validatePost(MinorClassPosts post) {
        Map<String, String> errors = new HashMap<>();

        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            errors.put("content", "Post content cannot be empty");
        }

        return errors;
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate createdDate) {
        String prefix = classId != null ? classId : "CLS";
        String year = String.format("%02d", createdDate.getYear() % 100);
        String date = String.format("%02d%02d", createdDate.getMonthValue(), createdDate.getDayOfMonth());
        String postId;
        SecureRandom random = new SecureRandom();

        do {
            String randomDigit = String.format("%03d", random.nextInt(1000));
            postId = prefix + year + date + randomDigit;
        } while (entityManager.find(ClassPosts.class, postId) != null);

        return postId;
    }
}