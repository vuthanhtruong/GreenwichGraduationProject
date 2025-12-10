package com.example.demo.post.minorClassPosts.service;

import com.example.demo.post.minorClassPosts.model.MinorClassPosts;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MinorClassPostsService {
    MinorClassPosts getMinorClassPost(String minorClassPostsId);
    void saveMinorClassPosts(MinorClassPosts minorClassPosts);
    List<MinorClassPosts> getClassPostByClass(String classId);
    Map<String, String> validatePost(MinorClassPosts post);
    String generateUniquePostId(String classId, LocalDate createdDate);
    List<String> getNotificationsForMemberId(String memberId);
    void deleteMinorClassPost(String minorClassPostsId);
}
