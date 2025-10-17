package com.example.demo.post.classPost.dao;

import com.example.demo.post.classPost.model.MajorClassPosts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class MajorClassPostsDAOImpl implements MajorClassPostsDAO {
    @Override
    public List<MajorClassPosts> getClassPostByClass(String classId) {
        return entityManager.createQuery("FROM MajorClassPosts m where m.majorClass.classId=:class",MajorClassPosts.class).
                setParameter("class",classId).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

}
