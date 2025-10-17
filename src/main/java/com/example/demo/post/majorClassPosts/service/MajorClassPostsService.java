package com.example.demo.post.majorClassPosts.service;

import com.example.demo.post.majorClassPosts.model.MajorClassPosts;

import java.util.List;

public interface MajorClassPostsService {
    List<MajorClassPosts> getClassPostByClass(String classId);
}
