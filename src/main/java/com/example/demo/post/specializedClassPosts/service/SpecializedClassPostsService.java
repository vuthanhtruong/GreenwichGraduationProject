package com.example.demo.post.specializedClassPosts.service;

import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpecializedClassPostsService {
    List<SpecializedClassPosts> getClassPostsByClass(String classId);
    void saveSpecializedClassPosts(SpecializedClassPosts specializedClassPosts);
    SpecializedClassPosts getSpecializedClassPost(String postId);
    Map<String, String> validatePost(SpecializedClassPosts post);
    String generateUniquePostId(String classId, LocalDate createdDate);
}
