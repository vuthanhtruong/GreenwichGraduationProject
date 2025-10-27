package com.example.demo.post.specializedAssignmentSubmitSlots.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.document.service.ClassDocumentsService;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
import com.example.demo.post.specializedAssignmentSubmitSlots.service.SpecializedAssignmentSubmitSlotsService;
import com.example.demo.post.specializedClassPosts.model.SpecializedClassPosts;
import com.example.demo.post.specializedClassPosts.service.SpecializedClassPostsService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.service.EmployesService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/classroom")
public class SpecializedAssignmentSubmitSlotsController {
    private final ClassesService classesService;
    private final MajorClassPostsService majorClassPostsService;
    private final SpecializedClassPostsService specializedClassPostsService;
    private final EmployesService employesService;
    private final ClassDocumentsService classDocumentsService;
    private final SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService;

    public SpecializedAssignmentSubmitSlotsController(ClassesService classesService, MajorClassPostsService majorClassPostsService, SpecializedClassPostsService specializedClassPostsService, EmployesService employesService, ClassDocumentsService classDocumentsService, SpecializedAssignmentSubmitSlotsService specializedAssignmentSubmitSlotsService) {
        this.classesService = classesService;
        this.majorClassPostsService = majorClassPostsService;
        this.specializedClassPostsService = specializedClassPostsService;
        this.employesService = employesService;
        this.classDocumentsService = classDocumentsService;
        this.specializedAssignmentSubmitSlotsService = specializedAssignmentSubmitSlotsService;
    }

    @PostMapping("/create-specialized-assignment-slot")
    public String createSpecializedAssignmentSlot(
            @RequestParam("classId") String classId,
            @Valid @ModelAttribute("slot") SpecializedAssignmentSubmitSlots slot,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Classes classes = classesService.findClassById(classId);
            if (classes == null) {
                model.addAttribute("errors", List.of("Class not found"));
                model.addAttribute("classes", new SpecializedClasses());
                model.addAttribute("ClassPostsList", new ArrayList<>());
                model.addAttribute("openSpecializedSlotOverlay", true);
                return "SpecializedLecturerClassroom";
            }

            if (!(classes instanceof SpecializedClasses specializedClasses)) {
                model.addAttribute("post", new SpecializedClassPosts());
                model.addAttribute("errors", List.of("Class is not a SpecializedClass"));
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openSpecializedSlotOverlay", true);
                return "MajorLecturerClassroom";
            }

            MajorEmployes creator = employesService.getMajorEmployee();
            if (creator == null) {
                model.addAttribute("post", new SpecializedClassPosts());
                model.addAttribute("errors", List.of("No authenticated MajorEmployes found"));
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                model.addAttribute("openSpecializedSlotOverlay", true);
                return "SpecializedLecturerClassroom";
            }

            slot.setCreator(creator);
            slot.setClassEntity(specializedClasses);

            Map<String, String> errors = specializedAssignmentSubmitSlotsService.validateSlot(slot);
            List<String> errorList = new ArrayList<>(errors.values());

            if (!errorList.isEmpty()) {
                model.addAttribute("post", new SpecializedClassPosts());
                model.addAttribute("errors", errorList);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                model.addAttribute("openSpecializedSlotOverlay", true);
                return "SpecializedLecturerClassroom";
            }

            slot.setPostId(specializedAssignmentSubmitSlotsService.generateUniquePostId(classId, LocalDate.now()));
            slot.setCreatedAt(LocalDateTime.now());

            specializedAssignmentSubmitSlotsService.save(slot);

            if (files != null && files.length > 0) {
                if (files.length > 5) {
                    model.addAttribute("post", new SpecializedClassPosts());
                    model.addAttribute("errors", List.of("Cannot upload more than 5 files"));
                    model.addAttribute("post", new SpecializedClassPosts());
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                    model.addAttribute("openSpecializedSlotOverlay", true);
                    return "SpecializedLecturerClassroom";
                }

                List<String> fileErrors = classDocumentsService.saveDocuments(slot, files);
                if (!fileErrors.isEmpty()) {
                    model.addAttribute("post", new SpecializedClassPosts());
                    model.addAttribute("errors", fileErrors);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
                    model.addAttribute("openSpecializedSlotOverlay", true);
                    model.addAttribute("post", new SpecializedClassPosts());
                    return "SpecializedLecturerClassroom";
                }
            }

            session.setAttribute("classId", classId);
            redirectAttributes.addFlashAttribute("message", "Specialized assignment submit slot created successfully!");
            return "redirect:/classroom";
        } catch (Exception e) {
            model.addAttribute("post", new SpecializedClassPosts());
            model.addAttribute("errors", List.of("Failed to create specialized assignment slot: " + e.getMessage()));
            model.addAttribute("classes", classesService.findClassById(classId));
            model.addAttribute("ClassPostsList", specializedClassPostsService.getClassPostsByClass(classId));
            model.addAttribute("post", new SpecializedClassPosts());
            model.addAttribute("openSpecializedSlotOverlay", true);
            return "SpecializedLecturerClassroom";
        }
    }
}
