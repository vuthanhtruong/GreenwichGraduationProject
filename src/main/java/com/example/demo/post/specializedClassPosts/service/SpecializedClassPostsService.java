package com.example.demo.post.specializedClassPosts.service;

import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;

import java.util.List;

public interface SpecializedClassPostsService {
    List<SpecializedClassPosts> getClassPostsByClass(String classId);
}
