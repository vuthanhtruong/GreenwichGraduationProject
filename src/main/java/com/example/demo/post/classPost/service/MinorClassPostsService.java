package com.example.demo.post.classPost.service;

import com.example.demo.post.classPost.model.MinorClassPosts;

import java.util.List;

public interface MinorClassPostsService {
    List<MinorClassPosts> getClassPostByClass(String classId);
}
