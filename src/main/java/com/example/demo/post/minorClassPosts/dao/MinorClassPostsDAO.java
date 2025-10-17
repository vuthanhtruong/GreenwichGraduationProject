package com.example.demo.post.minorClassPosts.dao;

import com.example.demo.post.minorClassPosts.model.MinorClassPosts;

import java.util.List;

public interface MinorClassPostsDAO {
    List<MinorClassPosts> getClassPostByClass(String classId);
}
