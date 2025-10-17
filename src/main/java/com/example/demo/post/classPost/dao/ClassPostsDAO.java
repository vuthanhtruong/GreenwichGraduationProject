package com.example.demo.post.classPost.dao;

import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.post.classPost.model.ClassPosts;

import java.util.List;

public interface ClassPostsDAO {
    List<ClassPosts> getClassPostsByClassId(Classes classes);
}
