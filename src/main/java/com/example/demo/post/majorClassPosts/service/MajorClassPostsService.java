package com.example.demo.post.majorClassPosts.service;

import com.example.demo.post.majorClassPosts.model.MajorClassPosts;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MajorClassPostsService {
    List<MajorClassPosts> getClassPostByClass(String classId);
    void saveMajorClassPosts(MajorClassPosts majorClassPosts);
    MajorClassPosts getMajorClassPost(String majorClassPostsId);
    Map<String, String> validatePost(MajorClassPosts post);
    String generateUniquePostId(String classId, LocalDate createdDate);
}
