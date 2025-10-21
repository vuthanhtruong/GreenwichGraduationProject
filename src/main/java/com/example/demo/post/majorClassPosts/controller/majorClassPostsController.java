package com.example.demo.post.majorClassPosts.controller;

import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.abstractClass.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.comment.model.MajorComments;
import com.example.demo.document.service.ClassDocumentsService;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/major-lecturer-home/classes-list/classroom")
public class majorClassPostsController {

    private final ClassesService classesService;
    private final MajorClassPostsService majorClassPostsService;
    private final SpecializedClassPostsService specializedClassPostsService;
    private final EmployesService employesService;
    private final ClassDocumentsService classDocumentsService;

    public majorClassPostsController(ClassesService classesService, MajorClassPostsService majorClassPostsService,
                                     SpecializedClassPostsService specializedClassPostsService,
                                     EmployesService employesService, ClassDocumentsService classDocumentsService) {
        this.classesService = classesService;
        this.majorClassPostsService = majorClassPostsService;
        this.specializedClassPostsService = specializedClassPostsService;
        this.employesService = employesService;
        this.classDocumentsService = classDocumentsService;
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
                model.addAttribute("newComment", new MajorComments());
                return "MajorLecturerClassroom";
            }

            if (!(classes instanceof MajorClasses majorClasses)) {
                model.addAttribute("errors", List.of("Class is not a MajorClass"));
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("Slot", new AssignmentSubmitSlots());
                model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                model.addAttribute("openPostOverlay", true);
                model.addAttribute("newComment", new MajorComments());
                return "SpecializedLecturerClassroom";
            }

            MajorEmployes creator = employesService.getMajorEmployee();
            post.setCreator(creator);
            if (creator == null) {
                model.addAttribute("errors", List.of("No authenticated MajorEmployes found"));
                model.addAttribute("post", post);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openPostOverlay", true);
                model.addAttribute("newComment", new MajorComments());
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
                model.addAttribute("newComment", new MajorComments());
                return "MajorLecturerClassroom";
            }

            post.setPostId(majorClassPostsService.generateUniquePostId(classId, LocalDate.now()));
            post.setCreatedAt(LocalDateTime.now());

            majorClassPostsService.saveMajorClassPosts(post);

            // Handle file uploads
            if (files != null && files.length > 0) {
                if (files.length > 5) {
                    model.addAttribute("errors", List.of("Cannot upload more than 5 files"));
                    model.addAttribute("post", post);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                    model.addAttribute("openPostOverlay", true);
                    model.addAttribute("newComment", new MajorComments());
                    return "MajorLecturerClassroom";
                }

                List<String> fileErrors = classDocumentsService.saveDocuments(post, files);
                if (!fileErrors.isEmpty()) {
                    model.addAttribute("errors", fileErrors);
                    model.addAttribute("post", post);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                    model.addAttribute("openPostOverlay", true);
                    model.addAttribute("newComment", new MajorComments());
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
            model.addAttribute("newComment", new MajorComments());
            return "MajorLecturerClassroom";
        }
    }

}