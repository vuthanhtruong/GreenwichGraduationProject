package com.example.demo.post.majorClassPosts.service;

import com.example.demo.post.majorClassPosts.dao.MajorClassPostsDAO;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
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
