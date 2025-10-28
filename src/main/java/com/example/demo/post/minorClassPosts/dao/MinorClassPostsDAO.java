package com.example.demo.post.minorClassPosts.dao;

import com.example.demo.post.minorClassPosts.model.MinorClassPosts;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MinorClassPostsDAO {
    MinorClassPosts getMinorClassPost(String minorClassPostsId);
    void saveMinorClassPosts(MinorClassPosts minorClassPosts);
    List<MinorClassPosts> getClassPostByClass(String classId);
    Map<String, String> validatePost(MinorClassPosts post);
    String generateUniquePostId(String classId, LocalDate createdDate);
}
