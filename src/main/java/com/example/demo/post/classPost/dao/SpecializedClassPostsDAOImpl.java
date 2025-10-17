package com.example.demo.post.classPost.dao;

import com.example.demo.post.classPost.model.SpecializedClassPosts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SpecializedClassPostsDAOImpl implements SpecializedClassPostsDAO {
    @Override
    public List<SpecializedClassPosts> getClassPostsByClass(String classId) {
        return entityManager.createQuery("from SpecializedClassPosts s where s.specializedClass=:classId",SpecializedClassPosts.class)
                .setParameter("classId", classId).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

}
