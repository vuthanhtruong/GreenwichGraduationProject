package com.example.demo.post.classPost.service;

import com.example.demo.post.classPost.dao.MinorClassPostsDAO;
import com.example.demo.post.classPost.model.MinorClassPosts;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinorClassPostsServiceImpl implements MinorClassPostsService {
    private final MinorClassPostsDAO minorClassPostsDAO;

    public MinorClassPostsServiceImpl(MinorClassPostsDAO minorClassPostsDAO) {
        this.minorClassPostsDAO = minorClassPostsDAO;
    }

    @Override
    public List<MinorClassPosts> getClassPostByClass(String classId) {
        return minorClassPostsDAO.getClassPostByClass(classId);
    }
}
