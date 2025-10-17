package com.example.demo.classes.majorClasses.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.model.MinorClasses;
import com.example.demo.post.classPost.model.MajorClassPosts;
import com.example.demo.post.classPost.model.MinorClassPosts;
import com.example.demo.post.classPost.model.SpecializedClassPosts;
import com.example.demo.post.classPost.service.MajorClassPostsService;
import com.example.demo.post.classPost.service.MinorClassPostsService;
import com.example.demo.post.classPost.service.SpecializedClassPostsService;
import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.abstractClass.service.ClassesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/student-home/student-classes-list/classroom")
public class StudentClassroomController {

    private final ClassesService classesService;
    private final MajorClassPostsService majorClassPostsService;
    private final MinorClassPostsService minorClassPostsService;
    private final SpecializedClassPostsService specializedClassPostsService;

    public StudentClassroomController(ClassesService classesService, MajorClassPostsService majorClassPostsService, MinorClassPostsService minorClassPostsService, SpecializedClassPostsService specializedClassPostsService) {
        this.classesService = classesService;
        this.majorClassPostsService = majorClassPostsService;
        this.minorClassPostsService = minorClassPostsService;
        this.specializedClassPostsService = specializedClassPostsService;
    }

    @PostMapping
    public String showClassroom(@RequestParam("classId") String classId, Model model) {
        try {
            Classes classes = classesService.findClassById(classId);
            if(classes instanceof MajorClasses majorClasses){
                List<MajorClassPosts> majorClassPostsList = majorClassPostsService.getClassPostByClass(classId);
                model.addAttribute("ClassPostsList", majorClassPostsList);
            } else if (classes instanceof MinorClasses minorClasses) {
                List<MinorClassPosts> minorClassPostsList = minorClassPostsService.getClassPostByClass(classId);
                model.addAttribute("ClassPostsList", minorClassPostsList);
            }
            else{
                List<SpecializedClassPosts> specializedClassPosts = specializedClassPostsService.getClassPostsByClass(classId);
                model.addAttribute("ClassPostsList", specializedClassPosts);
            }
            model.addAttribute("classes", classes);
            return "Classroom";
        } catch (Exception e) {
            e.printStackTrace();
            return "Classroom";
        }
    }
}