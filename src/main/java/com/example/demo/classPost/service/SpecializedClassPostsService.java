package com.example.demo.classPost.service;

import com.example.demo.classPost.model.SpecializedClassPosts;

import java.util.List;

public interface SpecializedClassPostsService {
    List<SpecializedClassPosts> getClassPostsByClass(String classId);
}
