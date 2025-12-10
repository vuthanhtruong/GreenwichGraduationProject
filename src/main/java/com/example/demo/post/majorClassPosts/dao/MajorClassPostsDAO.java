package com.example.demo.post.majorClassPosts.dao;

import com.example.demo.post.majorClassPosts.model.MajorClassPosts;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MajorClassPostsDAO {
    List<MajorClassPosts> getClassPostByClass(String classId);
    void saveMajorClassPosts(MajorClassPosts majorClassPosts);
    MajorClassPosts getMajorClassPost(String majorClassPostsId);
    Map<String, String> validatePost(MajorClassPosts post);
    String generateUniquePostId(String classId, LocalDate createdDate);
    List<String> getNotificationsForMemberId(String memberId);
    void deleteMajorClassPost(String majorClassPostsId);
}
