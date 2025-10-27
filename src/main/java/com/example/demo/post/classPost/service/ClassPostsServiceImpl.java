package com.example.demo.post.classPost.service;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.post.classPost.dao.ClassPostsDAO;
import com.example.demo.post.classPost.model.ClassPosts;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassPostsServiceImpl implements ClassPostsService {
    @Override
    public void savePost(ClassPosts post) {
        classPostsDAO.savePost(post);
    }

    @Override
    public ClassPosts findPostById(String postId) {
        return classPostsDAO.findPostById(postId);
    }

    @Override
    public List<ClassPosts> getClassPostsByClassId(String classes) {
        return classPostsDAO.getClassPostsByClassId(classes);
    }

    @Override
    public List<ClassPosts> getPaginatedClassPostsByClassId(Classes classes, int firstResult, int pageSize) {
        return classPostsDAO.getPaginatedClassPostsByClassId(classes, firstResult, pageSize);
    }

    @Override
    public long countPostsByClassId(Classes classes) {
        return classPostsDAO.countPostsByClassId(classes);
    }

    @Override
    public void deletePost(String postId) {
        classPostsDAO.deletePost(postId);
    }

    @Override
    public boolean existsPostById(String postId) {
        return classPostsDAO.existsPostById(postId);
    }

    private final ClassPostsDAO classPostsDAO;

    public ClassPostsServiceImpl(ClassPostsDAO classPostsDAO) {
        this.classPostsDAO = classPostsDAO;
    }
}
