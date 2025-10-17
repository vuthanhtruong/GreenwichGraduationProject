package com.example.demo.post.classPost.dao;

import com.example.demo.post.classPost.model.MinorClassPosts;

import java.util.List;

public interface MinorClassPostsDAO {
    List<MinorClassPosts> getClassPostByClass(String classId);
}
