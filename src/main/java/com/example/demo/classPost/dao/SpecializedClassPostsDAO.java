package com.example.demo.classPost.dao;

import com.example.demo.classPost.model.SpecializedClassPosts;

import java.util.List;

public interface SpecializedClassPostsDAO {
    List<SpecializedClassPosts> getClassPostsByClass(String classId);
}
