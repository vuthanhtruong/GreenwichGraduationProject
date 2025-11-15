package com.example.demo.post.majorClassPosts.dao;

import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
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
public class MajorClassPostsDAOImpl implements MajorClassPostsDAO {

    @Override
    public List<String> getNotificationsForMemberId(String memberId) {
        // Query 1: Sinh viên
        String jpqlStudent = """
        SELECT CONCAT('New post in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... on ', p.createdAt)
        FROM MajorClassPosts p
        JOIN p.majorClass c
        JOIN Students_MajorClasses smc ON smc.majorClass.classId = c.classId
        JOIN smc.student s
        WHERE s.id = :memberId
          AND p.notificationType = 'MAJOR_POST_CREATED'
        """;

        // Query 2: Giảng viên
        String jpqlLecturer = """
        SELECT CONCAT('New post in ', c.nameClass, 
                      ' by ', p.creator.id, 
                      ': ', SUBSTRING(p.content, 1, 50), 
                      '... on ', p.createdAt)
        FROM MajorClassPosts p
        JOIN p.majorClass c
        JOIN MajorLecturers_MajorClasses lmc ON lmc.majorClass.classId = c.classId
        JOIN lmc.lecturer l
        WHERE l.id = :memberId
          AND p.notificationType = 'MAJOR_POST_CREATED'
        """;

        List<String> studentNotifs = entityManager.createQuery(jpqlStudent, String.class)
                .setParameter("memberId", memberId)
                .getResultList();

        List<String> lecturerNotifs = entityManager.createQuery(jpqlLecturer, String.class)
                .setParameter("memberId", memberId)
                .getResultList();

        // GỘP + SẮP XẾP THEO THỜI GIAN MỚI NHẤT
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MajorClassPosts getMajorClassPost(String majorClassPostsId) {
        return entityManager.find(MajorClassPosts.class, majorClassPostsId);
    }

    @Override
    public void saveMajorClassPosts(MajorClassPosts majorClassPosts) {
        entityManager.persist(majorClassPosts);
    }

    @Override
    public List<MajorClassPosts> getClassPostByClass(String classId) {
        return entityManager.createQuery("FROM MajorClassPosts m WHERE m.majorClass.classId = :classId", MajorClassPosts.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public Map<String, String> validatePost(MajorClassPosts post) {
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
            String randomDigit = String.format("%03d", random.nextInt(1000)); // 3 chữ số ngẫu nhiên
            postId = prefix + year + date + randomDigit;
        } while (entityManager.find(ClassPosts.class, postId) != null);
        return postId;
    }
}