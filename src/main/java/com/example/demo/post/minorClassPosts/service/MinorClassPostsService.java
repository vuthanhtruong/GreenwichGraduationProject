package com.example.demo.post.minorClassPosts.service;

import com.example.demo.post.minorClassPosts.model.MinorClassPosts;

import java.util.List;

public interface MinorClassPostsService {
    List<MinorClassPosts> getClassPostByClass(String classId);
}
