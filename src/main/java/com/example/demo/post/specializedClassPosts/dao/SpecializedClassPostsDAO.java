package com.example.demo.post.specializedClassPosts.dao;

import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;

import java.util.List;

public interface SpecializedClassPostsDAO {
    List<SpecializedClassPosts> getClassPostsByClass(String classId);
}
