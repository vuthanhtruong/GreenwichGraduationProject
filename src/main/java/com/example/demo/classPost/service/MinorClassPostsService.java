package com.example.demo.classPost.service;

import com.example.demo.classPost.model.MinorClassPosts;

import java.util.List;

public interface MinorClassPostsService {
    List<MinorClassPosts> getClassPostByClass(String classId);
}
