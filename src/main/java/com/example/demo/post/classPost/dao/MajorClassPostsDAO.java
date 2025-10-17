package com.example.demo.post.classPost.dao;

import com.example.demo.post.classPost.model.MajorClassPosts;

import java.util.List;

public interface MajorClassPostsDAO {
    List<MajorClassPosts> getClassPostByClass(String classId);
}
