package com.example.demo.classes.minorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
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
@RequestMapping("/deputy-staff-home/minor-classes")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class AddMinorClassController {

    private final MinorClassesService classesService;
    private final DeputyStaffsService deputyStaffsService;
    private final MinorSubjectsService subjectsService;

    @Autowired
    public AddMinorClassController(MinorClassesService classesService, DeputyStaffsService deputyStaffsService, MinorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.deputyStaffsService = deputyStaffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @RequestParam("nameClass") String nameClass,
            @RequestParam("slotQuantity") Integer slotQuantity,
            @RequestParam("subjectId") String subjectId,
            @RequestParam("session") Sessions session,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession httpSession) {

        List<String> errors = new ArrayList<>();
        MinorClasses newClass = new MinorClasses();

        // Basic input validation
        if (nameClass == null || nameClass.trim().isEmpty()) {
            errors.add("Class name cannot be empty.");
        } else {
            newClass.setNameClass(nameClass.trim());
        }

        if (slotQuantity == null || slotQuantity <= 0) {
            errors.add("Slot quantity must be a positive number.");
        } else {
            newClass.setSlotQuantity(slotQuantity);
        }

        MinorSubjects subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            errors.add("Invalid or missing subject selection.");
        } else {
            newClass.setMinorSubject(subject);
        }

        if (session == null) {
            errors.add("Session is required.");
        } else {
            newClass.setSession(session);
        }

        // Additional validation via service
        if (errors.isEmpty()) {
            errors.addAll(classesService.validateClass(newClass, null));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newClass", newClass);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("sessions", Sessions.values());

            // Retrieve and set pagination data using httpSession
            int pageSize = httpSession.getAttribute("classPageSize") != null ? (Integer) httpSession.getAttribute("classPageSize") : 5;
            model.addAttribute("classes", classesService.getPaginatedClasses(0, pageSize));
            model.addAttribute("currentPageClasses", httpSession.getAttribute("currentPageClasses") != null ? (Integer) httpSession.getAttribute("currentPageClasses") : 1);
            model.addAttribute("totalPagesClasses", httpSession.getAttribute("totalPagesClasses") != null ? (Integer) httpSession.getAttribute("totalPagesClasses") : 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", classesService.numberOfClasses());
            return "MinorClassesList";
        }

        try {
            // Generate unique class ID and set creation time
            String classId = classesService.generateUniqueClassId(LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreatedAt(LocalDateTime.now());

            // Persist the new class
            classesService.addClass(newClass);
            redirectAttributes.addFlashAttribute("successMessage", "Class added successfully!");
            return "redirect:/deputy-staff-home/minor-classes";
        } catch (IllegalArgumentException e) {
            errors.add("Validation error: " + e.getMessage());
        } catch (Exception e) {
            errors.add("An unexpected error occurred while adding the class: " + e.getMessage());
        }

        // Handle errors after the try-catch block
        model.addAttribute("openAddOverlay", true);
        model.addAttribute("errors", errors);
        model.addAttribute("newClass", newClass);
        model.addAttribute("subjects", subjectsService.getAllSubjects());
        model.addAttribute("sessions", Sessions.values());

        int pageSize = httpSession.getAttribute("classPageSize") != null ? (Integer) httpSession.getAttribute("classPageSize") : 5;
        model.addAttribute("classes", classesService.getPaginatedClasses(0, pageSize));
        model.addAttribute("currentPageClasses", httpSession.getAttribute("currentPageClasses") != null ? (Integer) httpSession.getAttribute("currentPageClasses") : 1);
        model.addAttribute("totalPagesClasses", httpSession.getAttribute("totalPagesClasses") != null ? (Integer) httpSession.getAttribute("totalPagesClasses") : 1);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalClasses", classesService.numberOfClasses());
        return "MinorClassesList";
    }
}