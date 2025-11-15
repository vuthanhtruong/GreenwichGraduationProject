package com.example.demo.post.specializedClassPosts.service;

import com.example.demo.post.specializedClassPosts.dao.SpecializedClassPostsDAO;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SpecializedClassPostsServiceImpl implements SpecializedClassPostsService {
    @Override
    public List<String> getNotificationsForMemberId(String memberId) {
        return specializedClassPostsDAO.getNotificationsForMemberId(memberId);
    }

    @Override
    public void saveSpecializedClassPosts(SpecializedClassPosts specializedClassPosts) {
        specializedClassPostsDAO.saveSpecializedClassPosts(specializedClassPosts);
    }

    @Override
    public SpecializedClassPosts getSpecializedClassPost(String postId) {
        return specializedClassPostsDAO.getSpecializedClassPost(postId);
    }

    @Override
    public Map<String, String> validatePost(SpecializedClassPosts post) {
        return specializedClassPostsDAO.validatePost(post);
    }

    @Override
    public String generateUniquePostId(String classId, LocalDate createdDate) {
        return specializedClassPostsDAO.generateUniquePostId(classId, createdDate);
    }

    private final SpecializedClassPostsDAO specializedClassPostsDAO;

    public SpecializedClassPostsServiceImpl(SpecializedClassPostsDAO specializedClassPostsDAO) {
        this.specializedClassPostsDAO = specializedClassPostsDAO;
    }

    @Override
    public List<SpecializedClassPosts> getClassPostsByClass(String classId) {
        return specializedClassPostsDAO.getClassPostsByClass(classId);
    }
}
