package com.example.demo.classes.controller;

import com.example.demo.classPost.model.MajorClassPosts;
import com.example.demo.classPost.service.MajorClassPostsService;
import com.example.demo.classes.model.Classes;
import com.example.demo.classes.service.ClassesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/student-home/student-classes-list/classroom")
public class ClassroomController {

    private final ClassesService classesService;
    private final MajorClassPostsService majorClassPostsService;

    public ClassroomController(ClassesService classesService, MajorClassPostsService majorClassPostsService) {
        this.classesService = classesService;
        this.majorClassPostsService = majorClassPostsService;
    }

    @PostMapping
    public String showClassroom(@RequestParam("classId") String classId, Model model) {
        try {
            Classes classes = classesService.findClassById(classId);
            List<MajorClassPosts> majorClassPostsList = majorClassPostsService.getClassPostByClass(classId);
            model.addAttribute("classes", classes);
            model.addAttribute("majorClassPostsList", majorClassPostsList);
            return "Classroom";
        } catch (Exception e) {
            e.printStackTrace();
            return "Classroom";
        }
    }
}