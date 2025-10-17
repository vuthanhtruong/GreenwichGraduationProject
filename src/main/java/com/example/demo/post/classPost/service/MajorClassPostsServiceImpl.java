package com.example.demo.post.classPost.service;

import com.example.demo.post.classPost.dao.MajorClassPostsDAO;
import com.example.demo.post.classPost.model.MajorClassPosts;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorClassPostsServiceImpl implements MajorClassPostsService {
    private final MajorClassPostsDAO majorClassPostsDAO;

    public MajorClassPostsServiceImpl(MajorClassPostsDAO majorClassPostsDAO) {
        this.majorClassPostsDAO = majorClassPostsDAO;
    }

    @Override
    public List<MajorClassPosts> getClassPostByClass(String classId) {
        return majorClassPostsDAO.getClassPostByClass(classId);
    }
}
