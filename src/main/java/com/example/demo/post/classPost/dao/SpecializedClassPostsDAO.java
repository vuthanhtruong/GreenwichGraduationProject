package com.example.demo.post.classPost.dao;

import com.example.demo.post.classPost.model.SpecializedClassPosts;

import java.util.List;

public interface SpecializedClassPostsDAO {
    List<SpecializedClassPosts> getClassPostsByClass(String classId);
}
