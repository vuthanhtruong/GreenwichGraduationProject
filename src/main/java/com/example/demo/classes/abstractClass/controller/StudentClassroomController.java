package com.example.demo.classes.abstractClass.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.minorClassPosts.model.MinorClassPosts;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.abstractClass.service.ClassesService;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.minorClassPosts.service.MinorClassPostsService;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student-home/student-classes-list/classroom")
public class StudentClassroomController {

    private final ClassesService classesService;
    private final MajorClassPostsService majorClassPostsService;
    private final MinorClassPostsService minorClassPostsService;
    private final SpecializedClassPostsService specializedClassPostsService;
    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService;

    public StudentClassroomController(ClassesService classesService, MajorClassPostsService majorClassPostsService, MinorClassPostsService minorClassPostsService, SpecializedClassPostsService specializedClassPostsService, AssignmentSubmitSlotsService assignmentSubmitSlotsService, SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService) {
        this.classesService = classesService;
        this.majorClassPostsService = majorClassPostsService;
        this.minorClassPostsService = minorClassPostsService;
        this.specializedClassPostsService = specializedClassPostsService;
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.specializedAssignmentSubmitSlotsService = specializedAssignmentSubmitSlotsService;
    }

    @PostMapping
    public String showClassroom(@RequestParam("classId") String classId, Model model) {
        try {
            Classes classes = classesService.findClassById(classId);
            List<ClassPosts> classPostsList = new ArrayList<>();

            if (classes instanceof MajorClasses majorClasses) {
                List<MajorClassPosts> majorClassPostsList = majorClassPostsService.getClassPostByClass(classId);
                List<AssignmentSubmitSlots> assignmentSubmitSlots = assignmentSubmitSlotsService.getAllAssignmentSubmitSlotsByClass(majorClasses);
                classPostsList.addAll(majorClassPostsList);
                classPostsList.addAll(assignmentSubmitSlots);
            } else if (classes instanceof MinorClasses) {
                List<MinorClassPosts> minorClassPostsList = minorClassPostsService.getClassPostByClass(classId);
                classPostsList.addAll(minorClassPostsList);
            } else if (classes instanceof SpecializedClasses specializedClasses) {
                List<SpecializedClassPosts> specializedClassPosts = specializedClassPostsService.getClassPostsByClass(classId);
                List<SpecializedAssignmentSubmitSlots> assignmentSubmitSlots = specializedAssignmentSubmitSlotsService.getAllSpecializedAssignmentSubmitSlotsByClass(specializedClasses);
                classPostsList.addAll(specializedClassPosts);
            }

            model.addAttribute("ClassPostsList", classPostsList);
            model.addAttribute("classes", classes);
            return "StudentClassroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to load classroom: " + e.getMessage()));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            return "StudentClassroom";
        }
    }
}