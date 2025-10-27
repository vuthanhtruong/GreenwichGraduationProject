package com.example.demo.post.majorAssignmentSubmitSlots.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.document.service.ClassDocumentsService;
import com.example.demo.post.majorClassPosts.model.MajorClassPosts;
import com.example.demo.post.majorClassPosts.service.MajorClassPostsService;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.post.majorAssignmentSubmitSlots.service.AssignmentSubmitSlotsService;
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
public class MajorAssignmentSubmitSlotsController {

    private final ClassesService classesService;
    private final MajorClassPostsService majorClassPostsService;
    private final AssignmentSubmitSlotsService assignmentSubmitSlotsService;
    private final EmployesService employesService;
    private final ClassDocumentsService classDocumentsService;

    public MajorAssignmentSubmitSlotsController(ClassesService classesService, MajorClassPostsService majorClassPostsService,
                                                AssignmentSubmitSlotsService assignmentSubmitSlotsService, EmployesService employesService,
                                                ClassDocumentsService classDocumentsService) {
        this.classesService = classesService;
        this.majorClassPostsService = majorClassPostsService;
        this.assignmentSubmitSlotsService = assignmentSubmitSlotsService;
        this.employesService = employesService;
        this.classDocumentsService = classDocumentsService;
    }

    @PostMapping("/create-major-assignment-slot")
    public String createMajorAssignmentSlot(
            @RequestParam("classId") String classId,
            @Valid @ModelAttribute("slot") AssignmentSubmitSlots slot,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Classes classes = classesService.findClassById(classId);
            if (classes == null) {
                model.addAttribute("errors", List.of("Class not found"));
                model.addAttribute("classes", new MajorClasses());
                model.addAttribute("ClassPostsList", new ArrayList<>());
                model.addAttribute("openSlotOverlay", true);
                return "MajorClassroom";
            }

            if (!(classes instanceof MajorClasses majorClasses)) {
                model.addAttribute("post", new MajorClassPosts());
                model.addAttribute("errors", List.of("Class is not a MajorClass"));
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openSlotOverlay", true);
                return "MajorClassroom";
            }

            MajorEmployes creator = employesService.getMajorEmployee();
            if (creator == null) {
                model.addAttribute("post", new MajorClassPosts());
                model.addAttribute("errors", List.of("No authenticated MajorEmployes found"));
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openSlotOverlay", true);
                return "MajorClassroom";
            }

            slot.setCreator(creator);
            slot.setClassEntity(majorClasses);

            Map<String, String> errors = assignmentSubmitSlotsService.validateSlot(slot);
            List<String> errorList = new ArrayList<>(errors.values());

            if (!errorList.isEmpty()) {
                model.addAttribute("post", new MajorClassPosts());
                model.addAttribute("errors", errorList);
                model.addAttribute("classes", classes);
                model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                model.addAttribute("openSlotOverlay", true);
                return "MajorClassroom";
            }

            slot.setPostId(assignmentSubmitSlotsService.generateUniquePostId(classId, LocalDate.now()));
            slot.setCreatedAt(LocalDateTime.now());

            assignmentSubmitSlotsService.save(slot);

            if (files != null && files.length > 0) {
                if (files.length > 5) {
                    model.addAttribute("post", new MajorClassPosts());
                    model.addAttribute("errors", List.of("Cannot upload more than 5 files"));
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                    model.addAttribute("openSlotOverlay", true);
                    return "MajorClassroom";
                }

                List<String> fileErrors = classDocumentsService.saveDocuments(slot, files);
                if (!fileErrors.isEmpty()) {
                    model.addAttribute("post", new MajorClassPosts());
                    model.addAttribute("errors", fileErrors);
                    model.addAttribute("classes", classes);
                    model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
                    model.addAttribute("openSlotOverlay", true);
                    return "MajorClassroom";
                }
            }

            session.setAttribute("classId", classId);
            redirectAttributes.addFlashAttribute("message", "Major assignment submit slot created successfully!");
            return "redirect:/classroom";
        } catch (Exception e) {
            model.addAttribute("post", new MajorClassPosts());
            model.addAttribute("errors", List.of("Failed to create major assignment slot: " + e.getMessage()));
            model.addAttribute("classes", classesService.findClassById(classId));
            model.addAttribute("ClassPostsList", majorClassPostsService.getClassPostByClass(classId));
            model.addAttribute("openSlotOverlay", true);
            return "MajorClassroom";
        }
    }
}