package com.example.demo.post.minorClassPosts.service;

import com.example.demo.post.minorClassPosts.dao.MinorClassPostsDAO;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MinorClassPostsServiceImpl implements MinorClassPostsService {

    private final MinorClassPostsDAO minorClassPostsDAO;

    public MinorClassPostsServiceImpl(MinorClassPostsDAO minorClassPostsDAO) {
        this.minorClassPostsDAO = minorClassPostsDAO;
    }

    @Override
    public MinorClassPosts getMinorClassPost(String minorClassPostsId) {
        return minorClassPostsDAO.getMinorClassPost(minorClassPostsId);
    }

    @Override
    public void saveMinorClassPosts(MinorClassPosts minorClassPosts) {
        minorClassPostsDAO.saveMinorClassPosts(minorClassPosts);
    }

    @Override
    public List<MinorClassPosts> getClassPostByClass(String classId) {
        return minorClassPostsDAO.getClassPostByClass(classId);
    }

    @Override
    public Map<String, String> validatePost(MinorClassPosts post) {
        return minorClassPostsDAO.validatePost(post);
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate createdDate) {
        return minorClassPostsDAO.generateUniquePostId(classId, createdDate);
    }
}