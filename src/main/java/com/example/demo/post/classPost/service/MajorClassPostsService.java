package com.example.demo.post.classPost.service;

import com.example.demo.post.classPost.model.MajorClassPosts;

import java.util.List;

public interface MajorClassPostsService {
    List<MajorClassPosts> getClassPostByClass(String classId);
}
