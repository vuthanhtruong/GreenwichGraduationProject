package com.example.demo.post.classPost.service;

import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.post.classPost.model.ClassPosts;

import java.util.List;

public interface ClassPostsService {
    void savePost(ClassPosts post);

    ClassPosts findPostById(String postId);

    List<ClassPosts> getClassPostsByClassId(Classes classes);

    List<ClassPosts> getPaginatedClassPostsByClassId(Classes classes, int firstResult, int pageSize);

    long countPostsByClassId(Classes classes);

    void deletePost(String postId);

    boolean existsPostById(String postId);
}
