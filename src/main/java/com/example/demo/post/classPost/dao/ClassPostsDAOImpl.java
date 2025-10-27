package com.example.demo.post.classPost.dao;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.post.majorClassPosts.dao.MajorClassPostsDAO;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ClassPostsDAOImpl implements ClassPostsDAO {

    private final MajorClassPostsService majorClassPostsService;
    private final MinorClassPostsService minorClassPostsService;
    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService;
    private final SpecializedClassPostsService specializedClassPostsService;
    private final MajorClassesService majorClassesService;
    private final MinorClassesService minorClassesService;

    @PersistenceContext
    private EntityManager entityManager;

    public ClassPostsDAOImpl(MajorClassPostsService majorClassPostsService, MinorClassPostsService minorClassPostsService, AssignmentSubmitSlotsService assignmentSubmitSlotsService, SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService, SpecializedClassPostsService specializedClassPostsService, MajorClassesService majorClassesService, MinorClassesService minorClassesService) {
        this.majorClassPostsService = majorClassPostsService;
        this.minorClassPostsService = minorClassPostsService;
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.specializedAssignmentSubmitSlotsService = specializedAssignmentSubmitSlotsService;
        this.specializedClassPostsService = specializedClassPostsService;
        this.majorClassesService = majorClassesService;
        this.minorClassesService = minorClassesService;
    }

    @Override
    public void savePost(ClassPosts post) {
        if (post == null || post.getPostId() == null) {
            throw new IllegalArgumentException("Post or Post ID cannot be null");
        }
        try {
            if (entityManager.find(ClassPosts.class, post.getPostId()) == null) {
                entityManager.persist(post);
            } else {
                entityManager.merge(post);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving post: " + e.getMessage(), e);
        }
    }

    @Override
    public ClassPosts findPostById(String postId) {
        if (postId == null) {
            return null;
        }
        try {
            return entityManager.find(ClassPosts.class, postId);
        } catch (Exception e) {
            throw new RuntimeException("Error finding post by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ClassPosts> getClassPostsByClassId(String classes) {
       List<ClassPosts> classPosts = new ArrayList<>();
       classPosts.addAll(majorClassPostsService.getClassPostByClass(classes));
       classPosts.addAll(minorClassPostsService.getClassPostByClass(classes));
       classPosts.addAll(assignmentSubmitSlotsService.getAssignmentSubmitSlotsByClass(classes));
       classPosts.addAll(specializedAssignmentSubmitSlotsService.getAllSpecializedAssignmentSubmitSlotsByClass(classes));
       classPosts.addAll(specializedClassPostsService.getClassPostsByClass(classes));
       return classPosts;
    }

    @Override
    public List<ClassPosts> getPaginatedClassPostsByClassId(Classes classes, int firstResult, int pageSize) {
        if (classes == null || classes.getClassId() == null || pageSize <= 0) {
            return List.of();
        }
        try {
            List<ClassPosts> posts = new ArrayList<>();
            String classId = classes.getClassId();

            if (classes instanceof MajorClasses) {
                // Lấy MajorClassPosts và AssignmentSubmitSlots
                List<MajorClassPosts> majorPosts = entityManager.createQuery(
                                "SELECT p FROM MajorClassPosts p WHERE p.majorClass.classId = :classId",
                                MajorClassPosts.class)
                        .setParameter("classId", classId)
                        .setFirstResult(firstResult)
                        .setMaxResults(pageSize)
                        .getResultList();
                posts.addAll(majorPosts);

                List<AssignmentSubmitSlots> assignmentSlots = entityManager.createQuery(
                                "SELECT s FROM AssignmentSubmitSlots s WHERE s.classEntity.classId = :classId",
                                AssignmentSubmitSlots.class)
                        .setParameter("classId", classId)
                        .setFirstResult(firstResult)
                        .setMaxResults(pageSize)
                        .getResultList();
                posts.addAll(assignmentSlots);

            } else if (classes instanceof SpecializedClasses) {
                // Lấy SpecializedClassPosts và SpecializedAssignmentSubmitSlots
                List<SpecializedClassPosts> specializedPosts = entityManager.createQuery(
                                "SELECT p FROM SpecializedClassPosts p WHERE p.specializedClass.classId = :classId",
                                SpecializedClassPosts.class)
                        .setParameter("classId", classId)
                        .setFirstResult(firstResult)
                        .setMaxResults(pageSize)
                        .getResultList();
                posts.addAll(specializedPosts);

                List<SpecializedAssignmentSubmitSlots> specializedSlots = entityManager.createQuery(
                                "SELECT s FROM SpecializedAssignmentSubmitSlots s WHERE s.classEntity.classId = :classId",
                                SpecializedAssignmentSubmitSlots.class)
                        .setParameter("classId", classId)
                        .setFirstResult(firstResult)
                        .setMaxResults(pageSize)
                        .getResultList();
                posts.addAll(specializedSlots);

            } else if (classes instanceof MinorClasses) {
                // Lấy MinorClassPosts
                List<MinorClassPosts> minorPosts = entityManager.createQuery(
                                "SELECT p FROM MinorClassPosts p WHERE p.minorClass.classId = :classId",
                                MinorClassPosts.class)
                        .setParameter("classId", classId)
                        .setFirstResult(firstResult)
                        .setMaxResults(pageSize)
                        .getResultList();
                posts.addAll(minorPosts);
            }

            // Sắp xếp theo createdAt giảm dần
            return posts.stream()
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding paginated posts by class ID: " + e.getMessage(), e);
        }
    }

    @Override
    public long countPostsByClassId(Classes classes) {
        if (classes == null || classes.getClassId() == null) {
            return 0;
        }
        try {
            String classId = classes.getClassId();
            long count = 0;

            if (classes instanceof MajorClasses) {
                count += entityManager.createQuery(
                                "SELECT COUNT(p) FROM MajorClassPosts p WHERE p.majorClass.classId = :classId",
                                Long.class)
                        .setParameter("classId", classId)
                        .getSingleResult();
                count += entityManager.createQuery(
                                "SELECT COUNT(s) FROM AssignmentSubmitSlots s WHERE s.classEntity.classId = :classId",
                                Long.class)
                        .setParameter("classId", classId)
                        .getSingleResult();
            } else if (classes instanceof SpecializedClasses) {
                count += entityManager.createQuery(
                                "SELECT COUNT(p) FROM SpecializedClassPosts p WHERE p.specializedClass.classId = :classId",
                                Long.class)
                        .setParameter("classId", classId)
                        .getSingleResult();
                count += entityManager.createQuery(
                                "SELECT COUNT(s) FROM SpecializedAssignmentSubmitSlots s WHERE s.classEntity.classId = :classId",
                                Long.class)
                        .setParameter("classId", classId)
                        .getSingleResult();
            } else if (classes instanceof MinorClasses) {
                count += entityManager.createQuery(
                                "SELECT COUNT(p) FROM MinorClassPosts p WHERE p.minorClass.classId = :classId",
                                Long.class)
                        .setParameter("classId", classId)
                        .getSingleResult();
            }

            return count;
        } catch (Exception e) {
            throw new RuntimeException("Error counting posts by class ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePost(String postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }
        try {
            ClassPosts post = findPostById(postId);
            if (post != null) {
                entityManager.remove(post);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting post: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsPostById(String postId) {
        if (postId == null) {
            return false;
        }
        try {
            return entityManager.find(ClassPosts.class, postId) != null;
        } catch (Exception e) {
            throw new RuntimeException("Error checking post existence: " + e.getMessage(), e);
        }
    }
}