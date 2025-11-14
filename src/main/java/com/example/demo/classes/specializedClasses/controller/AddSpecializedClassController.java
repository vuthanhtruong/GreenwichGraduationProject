package com.example.demo.classes.specializedClasses.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class AddSpecializedClassController {

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializedSubjectsService subjectsService;

    public AddSpecializedClassController(SpecializedClassesService classesService,
                                         StaffsService staffsService,
                                         SpecializedSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @Valid @ModelAttribute("newClass") SpecializedClasses newClass,
            BindingResult bindingResult,
            @RequestParam("specializedSubjectId") String specializedSubjectId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (specializedSubjectId != null && !specializedSubjectId.isBlank()) {
            var subject = subjectsService.getSubjectById(specializedSubjectId);
            if (subject == null) {
                bindingResult.rejectValue("specializedSubject", "invalid", "Invalid subject selected.");
            } else {
                newClass.setSpecializedSubject(subject);
            }
        }

        Map<String, String> serviceErrors = classesService.validateClass(newClass, null);

        if (bindingResult.hasErrors() || !serviceErrors.isEmpty()) {
            model.addAttribute("org.springframework.validation.BindingResult.newClass", bindingResult);
            model.addAttribute("serviceErrors", serviceErrors);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("newClass", newClass);
            model.addAttribute("specializedSubjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("sessions", Sessions.values());
            return loadClassesPage(model, session, 1);
        }

        try {
            String classId = classesService.generateUniqueClassId(
                    specializedSubjectId != null ? specializedSubjectId : "default",
                    LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreator(staffsService.getStaff());

            classesService.addClass(newClass);

            redirectAttributes.addFlashAttribute("successMessage", "Specialized class added successfully!");
            return "redirect:/staff-home/specialized-classes-list";
        } catch (Exception e) {
            model.addAttribute("serviceErrors", Map.of("general", "Failed to add class: " + e.getMessage()));
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("newClass", newClass);
            return loadClassesPage(model, session, 1);
        }
    }

    private String loadClassesPage(Model model, HttpSession session, int page) {
        Integer pageSize = (Integer) session.getAttribute("classPageSize");
        if (pageSize == null || pageSize <= 0) pageSize = 5;

        int firstResult = (page - 1) * pageSize;
        var major = staffsService.getStaffMajor();
        String campusId = staffsService.getCampusOfStaff().getCampusId();

        List<SpecializedClasses> classes = classesService.getPaginatedClassesByCampus(firstResult, pageSize, major, campusId);
        long totalClasses = classesService.numberOfClassesByCampus(major, campusId);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));

        model.addAttribute("classes", classes);
        model.addAttribute("specializedSubjects", subjectsService.subjectsByMajor(major));
        model.addAttribute("currentPageClasses", page);
        model.addAttribute("totalPagesClasses", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalClasses", totalClasses);
        model.addAttribute("sessions", Sessions.values());
        model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());

        return "SpecializedClassesList";
    }

    @ModelAttribute("newClass")
    public SpecializedClasses populateNewClass() {
        return new SpecializedClasses();
    }
}