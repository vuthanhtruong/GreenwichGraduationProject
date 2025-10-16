package com.example.demo.classPost.service;

import com.example.demo.classPost.dao.MinorClassPostsDAO;
import com.example.demo.classPost.model.MinorClassPosts;
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
