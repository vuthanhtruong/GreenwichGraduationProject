package com.example.demo.post.majorClassPosts.service;

import com.example.demo.post.majorClassPosts.dao.MajorClassPostsDAO;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MajorClassPostsServiceImpl implements MajorClassPostsService {
    @Override
    public List<String> getNotificationsForMemberId(String memberId) {
        return majorClassPostsDAO.getNotificationsForMemberId(memberId);
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate createdDate) {
        return majorClassPostsDAO.generateUniquePostId(classId, createdDate);
    }

    @Override
    public Map<String, String> validatePost(MajorClassPosts post) {
        return majorClassPostsDAO.validatePost(post);
    }

    @Override
    public MajorClassPosts getMajorClassPost(String majorClassPostsId) {
        return majorClassPostsDAO.getMajorClassPost(majorClassPostsId);
    }

    @Override
    public void saveMajorClassPosts(MajorClassPosts majorClassPosts) {
        majorClassPostsDAO.saveMajorClassPosts(majorClassPosts);
    }

    private final MajorClassPostsDAO majorClassPostsDAO;

    public MajorClassPostsServiceImpl(MajorClassPostsDAO majorClassPostsDAO) {
        this.majorClassPostsDAO = majorClassPostsDAO;
    }

    @Override
    public List<MajorClassPosts> getClassPostByClass(String classId) {
        return majorClassPostsDAO.getClassPostByClass(classId);
    }
}
