package com.example.demo.post.majorClassPosts.dao;

import com.example.demo.post.majorClassPosts.model.MajorClassPosts;

import java.util.List;

public interface MajorClassPostsDAO {
    List<MajorClassPosts> getClassPostByClass(String classId);
}
