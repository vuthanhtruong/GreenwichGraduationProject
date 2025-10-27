package com.example.demo.classes.abstractClasses.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.classPost.service.ClassPostsService;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {

    private final ClassesService classesService;
    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService;
    private final PersonsService personsService;
    private final MajorClassesService majorClassesService;
    private final SpecializedClassesService specializedClassesService;
    private final ClassPostsService classPostsService;

    public ClassroomController(ClassesService classesService,
                               AssignmentSubmitSlotsService assignmentSubmitSlotsService, SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService, PersonsService personsService, MajorClassesService majorClassesService, SpecializedClassesService specializedClassesService, ClassPostsService classPostsService) {
        this.classesService = classesService;
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.specializedAssignmentSubmitSlotsService = specializedAssignmentSubmitSlotsService;
        this.personsService = personsService;
        this.majorClassesService = majorClassesService;
        this.specializedClassesService = specializedClassesService;
        this.classPostsService = classPostsService;
    }

    @GetMapping
    public String showClassroomGet(HttpSession session, Model model) {
        try {
            if (personsService.getPerson() instanceof MajorLecturers) {
                model.addAttribute("home", "/major-lecturer-home");
                model.addAttribute("listClass", "/major-lecturer-home/classes-list");
                model.addAttribute("add", true);
            } else if (personsService.getPerson() instanceof Staffs) {
                model.addAttribute("home", "/staff-home");
                model.addAttribute("listClass", "/staff-home/classes-list");
                model.addAttribute("add", true);
            } else if (personsService.getPerson() instanceof Students) {
                model.addAttribute("home", "/student-home");
                model.addAttribute("listClass", "/student-home/student-classes-list");
                model.addAttribute("add", false);
            }

            String classId = (String) session.getAttribute("classId");
            if (classId == null) {
                model.addAttribute("errors", List.of("No class selected"));
                model.addAttribute("classes", new MajorClasses());
                model.addAttribute("ClassPostsList", new ArrayList<>());
                model.addAttribute("post", new MajorClassPosts());
                return "Classroom";
            }

            Classes classes = classesService.findClassById(classId);
            List<ClassPosts> classPostsList=classPostsService.getClassPostsByClassId(classId);

            if (majorClassesService.getClassById(classId) != null) {
                List<AssignmentSubmitSlots> assignmentSubmitSlots = assignmentSubmitSlotsService.getAllAssignmentSubmitSlotsByClass(majorClassesService.getClassById(classId));
                classPostsList.sort(Comparator.comparing(ClassPosts::getCreatedAt, Comparator.reverseOrder()));
                model.addAttribute("assignmentSubmitSlots", assignmentSubmitSlots);
                model.addAttribute("post", new MajorClassPosts());
                model.addAttribute("slot", new AssignmentSubmitSlots());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                model.addAttribute("addPostClass", "/classroom/upload-major-post");
                model.addAttribute("addASM", "/classroom/create-major-assignment-slot");
                return "MajorClassroom";
            } else if (specializedClassesService.getClassById(classId) != null) {
                List<SpecializedAssignmentSubmitSlots> assignmentSubmitSlots = specializedAssignmentSubmitSlotsService.getAllSpecializedAssignmentSubmitSlotsByClass(specializedClassesService.getClassById(classId));
                classPostsList.sort(Comparator.comparing(ClassPosts::getCreatedAt, Comparator.reverseOrder()));
                model.addAttribute("assignmentSubmitSlots", assignmentSubmitSlots);
                model.addAttribute("post", new SpecializedClassPosts());
                model.addAttribute("slot", new SpecializedAssignmentSubmitSlots());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                model.addAttribute("addPostClass", "/classroom/upload-specialized-post");
                model.addAttribute("addASM", "/classroom/create-specialized-assignment-slot");
                return "SpecializedClassroom";
            }

            model.addAttribute("errors", List.of("Invalid class type"));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "Classroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to load classroom: " + e.getMessage()));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "Classroom";
        }
    }

    @PostMapping
    public String showClassroomPost(@RequestParam("classId") String classId, HttpSession session, Model model) {
        try {
            if (personsService.getPerson() instanceof MajorLecturers) {
                model.addAttribute("home", "/major-lecturer-home");
                model.addAttribute("listClass", "/major-lecturer-home/classes-list");
                model.addAttribute("add", true);
            } else if (personsService.getPerson() instanceof Staffs) {
                model.addAttribute("home", "/staff-home");
                model.addAttribute("listClass", "/staff-home/classes-list");
                model.addAttribute("add", true);
            } else if (personsService.getPerson() instanceof Students) {
                model.addAttribute("home", "/student-home");
                model.addAttribute("listClass", "/student-home/student-classes-list");
                model.addAttribute("add", false);
            }
            session.setAttribute("classId", classId);
            Classes classes = classesService.findClassById(classId);
            List<ClassPosts> classPostsList=classPostsService.getClassPostsByClassId(classId);

            if (majorClassesService.getClassById(classId) != null) {
                List<AssignmentSubmitSlots> assignmentSubmitSlots = assignmentSubmitSlotsService.getAllAssignmentSubmitSlotsByClass(majorClassesService.getClassById(classId));
                classPostsList.sort(Comparator.comparing(ClassPosts::getCreatedAt, Comparator.reverseOrder()));
                model.addAttribute("assignmentSubmitSlots", assignmentSubmitSlots);
                model.addAttribute("post", new MajorClassPosts());
                model.addAttribute("slot", new AssignmentSubmitSlots());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                model.addAttribute("addPostClass", "/classroom/upload-major-post");
                model.addAttribute("addASM", "/classroom/create-major-assignment-slot");
                return "MajorClassroom";
            } else if (specializedClassesService.getClassById(classId) != null) {
                List<SpecializedAssignmentSubmitSlots> assignmentSubmitSlots = specializedAssignmentSubmitSlotsService.getAllSpecializedAssignmentSubmitSlotsByClass(specializedClassesService.getClassById(classId));
                classPostsList.sort(Comparator.comparing(ClassPosts::getCreatedAt, Comparator.reverseOrder()));
                model.addAttribute("assignmentSubmitSlots", assignmentSubmitSlots);
                model.addAttribute("post", new SpecializedClassPosts());
                model.addAttribute("slot", new SpecializedAssignmentSubmitSlots());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                model.addAttribute("addPostClass", "/classroom/upload-specialized-post");
                model.addAttribute("addASM", "/classroom/create-specialized-assignment-slot");
                return "SpecializedClassroom";
            }

            model.addAttribute("errors", List.of("Invalid class type"));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "Classroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to load classroom: " + e.getMessage()));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "Classroom";
        }
    }
}