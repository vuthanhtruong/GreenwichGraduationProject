package com.example.demo.classPost.dao;

import com.example.demo.classPost.model.MinorClassPosts;

import java.util.List;

public interface MinorClassPostsDAO {
    List<MinorClassPosts> getClassPostByClass(String classId);
}
