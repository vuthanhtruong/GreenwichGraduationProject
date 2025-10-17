package com.example.demo.post.classPost.service;

import com.example.demo.post.classPost.dao.SpecializedClassPostsDAO;
import com.example.demo.post.classPost.model.SpecializedClassPosts;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializedClassPostsServiceImpl implements SpecializedClassPostsService {
    private final SpecializedClassPostsDAO specializedClassPostsDAO;

    public SpecializedClassPostsServiceImpl(SpecializedClassPostsDAO specializedClassPostsDAO) {
        this.specializedClassPostsDAO = specializedClassPostsDAO;
    }

    @Override
    public List<SpecializedClassPosts> getClassPostsByClass(String classId) {
        return specializedClassPostsDAO.getClassPostsByClass(classId);
    }
}
