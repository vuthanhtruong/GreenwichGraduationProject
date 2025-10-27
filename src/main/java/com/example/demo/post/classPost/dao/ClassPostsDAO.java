package com.example.demo.post.classPost.dao;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.post.classPost.model.ClassPosts;

import java.util.List;

public interface ClassPostsDAO {
    void savePost(ClassPosts post);

    ClassPosts findPostById(String postId);

    List<ClassPosts> getClassPostsByClassId(String classes);

    List<ClassPosts> getPaginatedClassPostsByClassId(Classes classes, int firstResult, int pageSize);

    long countPostsByClassId(Classes classes);

    void deletePost(String postId);

    boolean existsPostById(String postId);
}
