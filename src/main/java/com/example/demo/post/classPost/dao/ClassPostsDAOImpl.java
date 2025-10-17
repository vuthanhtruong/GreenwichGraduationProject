package com.example.demo.post.classPost.dao;

import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.post.classPost.model.ClassPosts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class ClassPostsDAOImpl implements ClassPostsDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ClassPosts> getClassPostsByClassId(Classes classes) {
        return null;
    }
}
