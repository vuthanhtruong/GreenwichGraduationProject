package com.example.demo.classPost.service;

import com.example.demo.classPost.model.MajorClassPosts;

import java.util.List;

public interface MajorClassPostsService {
    List<MajorClassPosts> getClassPostByClass(String classId);
}
