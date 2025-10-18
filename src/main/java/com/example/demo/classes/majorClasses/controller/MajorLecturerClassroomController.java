package com.example.demo.classes.majorClasses.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.document.service.ClassDocumentsService;
import com.example.demo.post.classPost.model.ClassPosts;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.abstractClass.service.ClassesService;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.service.EmployesService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/major-lecturer-home/classes-list/classroom")
public class MajorLecturerClassroomController {

    private final ClassesService classesService;
    private final MajorClassPostsService majorClassPostsService;
    private final SpecializedClassPostsService specializedClassPostsService;
    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final EmployesService employesService;
    private final ClassDocumentsService classDocumentsService;

    public MajorLecturerClassroomController(ClassesService classesService, MajorClassPostsService majorClassPostsService,
                                            SpecializedClassPostsService specializedClassPostsService,
                                            AssignmentSubmitSlotsService assignmentSubmitSlotsService,
                                            EmployesService employesService, ClassDocumentsService classDocumentsService) {
        this.classesService = classesService;
        this.majorClassPostsService = majorClassPostsService;
        this.specializedClassPostsService = specializedClassPostsService;
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.employesService = employesService;
        this.classDocumentsService = classDocumentsService;
    }

    @GetMapping
    public String showClassroomGet(HttpSession session, Model model) {
        try {
            String classId = (String) session.getAttribute("classId");
            if (classId == null) {
                model.addAttribute("errors", List.of("No class selected"));
                model.addAttribute("classes", new MajorClasses());
                model.addAttribute("ClassPostsList", new ArrayList<>());
                model.addAttribute("post", new MajorClassPosts());
                return "MajorLecturerClassroom";
            }

            Classes classes = classesService.findClassById(classId);
            List<ClassPosts> classPostsList = new ArrayList<>();

            if (classes instanceof MajorClasses majorClasses) {
                List<MajorClassPosts> majorClassPostsList = majorClassPostsService.getClassPostByClass(classId);
                List<AssignmentSubmitSlots> assignmentSubmitSlots = assignmentSubmitSlotsService.getAllAssignmentSubmitSlotsByClass(majorClasses);
                classPostsList.addAll(majorClassPostsList);
                classPostsList.addAll(assignmentSubmitSlots);
                model.addAttribute("post", new MajorClassPosts());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                return "MajorLecturerClassroom";
            } else if (classes instanceof SpecializedClasses) {
                List<SpecializedClassPosts> specializedClassPosts = specializedClassPostsService.getClassPostsByClass(classId);
                classPostsList.addAll(specializedClassPosts);
                model.addAttribute("post", new SpecializedClassPosts());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                return "SpecializedLecturerClassroom";
            }

            model.addAttribute("errors", List.of("Invalid class type"));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "MajorLecturerClassroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to load classroom: " + e.getMessage()));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "MajorLecturerClassroom";
        }
    }

    @PostMapping
    public String showClassroomPost(@RequestParam("classId") String classId, HttpSession session, Model model) {
        try {
            session.setAttribute("classId", classId);
            Classes classes = classesService.findClassById(classId);
            List<ClassPosts> classPostsList = new ArrayList<>();

            if (classes instanceof MajorClasses majorClasses) {
                List<MajorClassPosts> majorClassPostsList = majorClassPostsService.getClassPostByClass(classId);
                List<AssignmentSubmitSlots> assignmentSubmitSlots = assignmentSubmitSlotsService.getAllAssignmentSubmitSlotsByClass(majorClasses);
                classPostsList.addAll(majorClassPostsList);
                classPostsList.addAll(assignmentSubmitSlots);
                model.addAttribute("post", new MajorClassPosts());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                return "MajorLecturerClassroom";
            } else if (classes instanceof SpecializedClasses) {
                List<SpecializedClassPosts> specializedClassPosts = specializedClassPostsService.getClassPostsByClass(classId);
                classPostsList.addAll(specializedClassPosts);
                model.addAttribute("post", new SpecializedClassPosts());
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", classPostsList);
                return "SpecializedLecturerClassroom";
            }

            model.addAttribute("errors", List.of("Invalid class type"));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "MajorLecturerClassroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to load classroom: " + e.getMessage()));
            model.addAttribute("classes", new MajorClasses());
            model.addAttribute("ClassPostsList", new ArrayList<>());
            model.addAttribute("post", new MajorClassPosts());
            return "MajorLecturerClassroom";
        }
    }

    @PostMapping("/upload-major-post")
    public String uploadMajorPost(
            @RequestParam("classId") String classId,
            @Valid @ModelAttribute("post") MajorClassPosts post,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Classes classes = classesService.findClassById(classId);
            if (classes == null) {
                model.addAttribute("errors", List.of("Class not found"));
                model.addAttribute("post", post);
                model.addAttribute("classes", new MajorClasses());
                model.addAttribute("ClassPostsList", new ArrayList<>());
                model.addAttribute("openPostOverlay", true);
                return "MajorLecturerClassroom";
            }

            if (!(classes instanceof MajorClasses majorClasses)) {
                model.addAttribute("errors", List.of("Class is not a MajorClass"));
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                model.addAttribute("openPostOverlay", true);
                return "SpecializedLecturerClassroom";
            }

            MajorEmployes creator = employesService.getMajorEmployee();
            if (creator == null) {
                model.addAttribute("errors", List.of("No authenticated MajorEmployes found"));
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openPostOverlay", true);
                return "MajorLecturerClassroom";
            }

            Map<String, String> errors = majorClassPostsService.validatePost(post);
            List<String> errorList = new ArrayList<>(errors.values());
            post.setMajorClass(majorClasses);

            if (!errorList.isEmpty()) {
                model.addAttribute("errors", errorList);
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openPostOverlay", true);
                return "MajorLecturerClassroom";
            }

            post.setPostId(majorClassPostsService.generateUniquePostId(classId, LocalDate.now()));
            post.setCreatedAt(LocalDateTime.now());
            post.setCreator(creator);

            majorClassPostsService.saveMajorClassPosts(post);

            // Handle file uploads
            if (files != null && files.length > 0) {
                if (files.length > 5) {
                    model.addAttribute("errors", List.of("Cannot upload more than 5 files"));
                    model.addAttribute("post", post);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                    model.addAttribute("openPostOverlay", true);
                    return "MajorLecturerClassroom";
                }

                List<String> fileErrors = classDocumentsService.saveDocuments(post, files);
                if (!fileErrors.isEmpty()) {
                    model.addAttribute("errors", fileErrors);
                    model.addAttribute("post", post);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                    model.addAttribute("openPostOverlay", true);
                    return "MajorLecturerClassroom";
                }
            }

            session.setAttribute("classId", classId);
            redirectAttributes.addFlashAttribute("message", "Major post created successfully!");
            return "redirect:/major-lecturer-home/classes-list/classroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to create major post: " + e.getMessage()));
            model.addAttribute("post", post);
            model.addAttribute("classes", classesService.findClassById(classId));
            model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
            model.addAttribute("openPostOverlay", true);
            return "MajorLecturerClassroom";
        }
    }

    @PostMapping("/upload-specialized-post")
    public String uploadSpecializedPost(
            @RequestParam("classId") String classId,
            @Valid @ModelAttribute("post") SpecializedClassPosts post,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Classes classes = classesService.findClassById(classId);
            if (classes == null) {
                model.addAttribute("errors", List.of("Class not found"));
                model.addAttribute("post", post);
                model.addAttribute("classes", new MajorClasses());
                model.addAttribute("ClassPostsList", new ArrayList<>());
                model.addAttribute("openPostOverlay", true);
                return "SpecializedLecturerClassroom";
            }

            if (!(classes instanceof SpecializedClasses specializedClasses)) {
                model.addAttribute("errors", List.of("Class is not a SpecializedClass"));
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openPostOverlay", true);
                return "MajorLecturerClassroom";
            }

            MajorEmployes creator = employesService.getMajorEmployee();
            if (creator == null) {
                model.addAttribute("errors", List.of("No authenticated MajorEmployes found"));
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                model.addAttribute("openPostOverlay", true);
                return "SpecializedLecturerClassroom";
            }
            post.setSpecializedClass(specializedClasses);

            Map<String, String> errors = specializedClassPostsService.validatePost(post);
            List<String> errorList = new ArrayList<>(errors.values());

            if (!errorList.isEmpty()) {
                model.addAttribute("errors", errorList);
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                model.addAttribute("openPostOverlay", true);
                return "SpecializedLecturerClassroom";
            }

            post.setPostId(specializedClassPostsService.generateUniquePostId(classId, LocalDate.now()));
            post.setCreatedAt(LocalDateTime.now());
            post.setCreator(creator);

            specializedClassPostsService.saveSpecializedClassPosts(post);

            // Handle file uploads
            if (files != null && files.length > 0) {
                if (files.length > 5) {
                    model.addAttribute("errors", List.of("Cannot upload more than 5 files"));
                    model.addAttribute("post", post);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                    model.addAttribute("openPostOverlay", true);
                    return "SpecializedLecturerClassroom";
                }

                List<String> fileErrors = classDocumentsService.saveDocuments(post, files);
                if (!fileErrors.isEmpty()) {
                    model.addAttribute("errors", fileErrors);
                    model.addAttribute("post", post);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                    model.addAttribute("openPostOverlay", true);
                    return "SpecializedLecturerClassroom";
                }
            }

            session.setAttribute("classId", classId);
            redirectAttributes.addFlashAttribute("message", "Specialized post created successfully!");
            return "redirect:/major-lecturer-home/classes-list/classroom";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Failed to create specialized post: " + e.getMessage()));
            model.addAttribute("post", post);
            model.addAttribute("classes", classesService.findClassById(classId));
            model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
            model.addAttribute("openPostOverlay", true);
            return "SpecializedLecturerClassroom";
        }
    }
}