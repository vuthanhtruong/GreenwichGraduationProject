package com.example.demo.classes.minorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class AddMinorClassController {

    private final MinorClassesService classesService;
    private final MinorSubjectsService subjectsService;

    @Autowired
    public AddMinorClassController(MinorClassesService classesService, MinorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @RequestParam("nameClass") String nameClass,
            @RequestParam("slotQuantity") Integer slotQuantity,
            @RequestParam("subjectId") String subjectId,
            @RequestParam("session") Sessions sessionEnum,  // Đổi tên: tránh trùng
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession httpSession) {  // Đổi tên: httpSession

        List<String> errors = new ArrayList<>();
        MinorClasses newClass = new MinorClasses();
        newClass.setNameClass(nameClass);
        newClass.setSlotQuantity(slotQuantity);
        newClass.setMinorSubject(subjectsService.getSubjectById(subjectId));
        newClass.setSession(sessionEnum);  // Dùng sessionEnum

        errors.addAll(classesService.validateClass(newClass, null));

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("sessions", Sessions.values());

            // Pagination from httpSession
            Integer pageSize = (Integer) httpSession.getAttribute("classPageSize");
            if (pageSize == null) pageSize = 5;

            model.addAttribute("classes", classesService.getPaginatedClasses(0, pageSize));
            model.addAttribute("currentPageClasses", httpSession.getAttribute("currentPageClasses") != null ? httpSession.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", httpSession.getAttribute("totalPagesClasses") != null ? httpSession.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", classesService.numberOfClasses());

            return "MinorClassesList";
        }

        try {
            String classId = classesService.generateUniqueClassId(LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreatedAt(LocalDateTime.now());

            classesService.addClass(newClass);
            redirectAttributes.addFlashAttribute("successMessage", "Minor class added successfully!");
            return "redirect:/deputy-staff-home/minor-classes-list";
        } catch (Exception e) {
            errors.add("Failed to add class: " + e.getMessage());
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("sessions", Sessions.values());

            Integer pageSize = (Integer) httpSession.getAttribute("classPageSize");
            if (pageSize == null) pageSize = 5;

            model.addAttribute("classes", classesService.getPaginatedClasses(0, pageSize));
            model.addAttribute("currentPageClasses", httpSession.getAttribute("currentPageClasses") != null ? httpSession.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", httpSession.getAttribute("totalPagesClasses") != null ? httpSession.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", classesService.numberOfClasses());

            return "MinorClassesList";
        }
    }
}