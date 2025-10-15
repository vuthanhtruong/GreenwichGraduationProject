package com.example.demo.classPost.dao;

import com.example.demo.classPost.model.MajorClassPosts;

import java.util.List;

public interface MajorClassPostsDAO {
    List<MajorClassPosts> getClassPostByClass(String classId);
}
