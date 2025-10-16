package com.example.demo.classPost.dao;

import com.example.demo.classPost.model.MinorClassPosts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class MinorClassPostsDAOImpl implements MinorClassPostsDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MinorClassPosts> getClassPostByClass(String classId) {
        return entityManager.createQuery("FROM MinorClassPosts m where m.minorClass=:classId", MinorClassPosts.class).setParameter("classId", classId).getResultList();
    }
}
