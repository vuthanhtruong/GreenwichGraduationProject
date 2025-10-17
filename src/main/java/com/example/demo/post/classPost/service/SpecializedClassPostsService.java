package com.example.demo.post.classPost.service;

import com.example.demo.post.classPost.model.SpecializedClassPosts;

import java.util.List;

public interface SpecializedClassPostsService {
    List<SpecializedClassPosts> getClassPostsByClass(String classId);
}
