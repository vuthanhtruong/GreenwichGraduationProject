package com.example.demo.classes.specializedClasses.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class AddSpecializedClassController {

    private static final Logger log = LoggerFactory.getLogger(AddSpecializedClassController.class);

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializedSubjectsService specializedSubjectsService;

    @Autowired
    public AddSpecializedClassController(SpecializedClassesService classesService,
                                         StaffsService staffsService,
                                         SpecializedSubjectsService specializedSubjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.specializedSubjectsService = specializedSubjectsService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @RequestParam("nameClass") String nameClass,
            @RequestParam("slotQuantity") Integer slotQuantity,
            @RequestParam("specializedSubjectId") String specializedSubjectId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();
        SpecializedClasses newClass = new SpecializedClasses();
        newClass.setNameClass(nameClass);
        newClass.setSlotQuantity(slotQuantity);
        var specializedSubject = specializedSubjectsService.getSubjectById(specializedSubjectId);
        newClass.setSpecializedSubject(specializedSubject);

        // Kiểm tra null cho specializedSubject
        if (specializedSubject == null) {
            errors.add("Invalid specialized subject selected.");
            log.warn("Specialized subject with ID: {} not found", specializedSubjectId);
        }

        // Validate lớp mới
        errors.addAll(classesService.validateClass(newClass, newClass.getClassId()));

        if (!errors.isEmpty()) {
            log.warn("Validation errors when adding class: {}", String.join("; ", errors));
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("specializedSubjects", specializedSubjectsService.subjectsByMajor(staffsService.getStaff().getMajorManagement()));
            model.addAttribute("classes", classesService.getPaginatedClasses(0, (Integer) session.getAttribute("classPageSize") != null ? (Integer) session.getAttribute("classPageSize") : 5, staffsService.getStaff().getMajorManagement()));
            model.addAttribute("currentPageClasses", session.getAttribute("currentPageClasses") != null ? session.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", session.getAttribute("totalPagesClasses") != null ? session.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", classesService.numberOfClasses(staffsService.getStaff().getMajorManagement()));
            return "SpecializedClassesList";
        }

        try {
            String specializedSubjectIdSafe = specializedSubjectId != null ? specializedSubjectId : "default";
            String classId = classesService.generateUniqueClassId(specializedSubjectIdSafe, LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreatedAt(LocalDateTime.now());

            classesService.addClass(newClass);
            log.info("Successfully added class with ID: {}", classId);
            redirectAttributes.addFlashAttribute("successMessage", "Class added successfully!");
            return "redirect:/staff-home/specialized-classes-list";
        } catch (Exception e) {
            log.error("Error adding class: {}", e.getMessage(), e);
            errors.add("Failed to add class: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("specializedSubjects", specializedSubjectsService.subjectsByMajor(staffsService.getStaff().getMajorManagement()));
            model.addAttribute("classes", classesService.getPaginatedClasses(0, (Integer) session.getAttribute("classPageSize") != null ? (Integer) session.getAttribute("classPageSize") : 5, staffsService.getStaff().getMajorManagement()));
            model.addAttribute("currentPageClasses", session.getAttribute("currentPageClasses") != null ? session.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", session.getAttribute("totalPagesClasses") != null ? session.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", classesService.numberOfClasses(staffsService.getStaff().getMajorManagement()));
            return "SpecializedClassesList";
        }
    }
}