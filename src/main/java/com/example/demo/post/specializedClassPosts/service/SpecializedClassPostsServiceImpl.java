package com.example.demo.post.specializedClassPosts.service;

import com.example.demo.post.specializedClassPosts.dao.SpecializedClassPostsDAO;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
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
