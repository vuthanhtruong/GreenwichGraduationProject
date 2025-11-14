package com.example.demo.classes.minorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class AddMinorClassController {

    private final MinorClassesService classesService;
    private final MinorSubjectsService subjectsService;
    private final DeputyStaffsService staffsService;

    public AddMinorClassController(MinorClassesService classesService,
                                   MinorSubjectsService subjectsService,
                                   DeputyStaffsService staffsService) {
        this.classesService = classesService;
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @Valid @ModelAttribute("newClass") MinorClasses newClass,
            BindingResult bindingResult,
            @RequestParam("subjectId") String subjectId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (subjectId != null && !subjectId.isBlank()) {
            newClass.setMinorSubject(subjectsService.getSubjectById(subjectId));
        }

        Map<String, String> serviceErrors = classesService.validateClass(newClass, null);

        if (bindingResult.hasErrors() || !serviceErrors.isEmpty()) {
            model.addAttribute("org.springframework.validation.BindingResult.newClass", bindingResult);
            model.addAttribute("serviceErrors", serviceErrors);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("newClass", newClass);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("sessions", Sessions.values());
            return loadClassesPage(model, session, 1);
        }

        try {
            String classId = classesService.generateUniqueClassId(LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreator(staffsService.getDeputyStaff());
            classesService.addClass(newClass);

            redirectAttributes.addFlashAttribute("successMessage", "Minor class added successfully!");
            return "redirect:/deputy-staff-home/minor-classes-list";
        } catch (Exception e) {
            model.addAttribute("serviceErrors", Map.of("general", "Failed to add minor class: " + e.getMessage()));
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("newClass", newClass);
            return loadClassesPage(model, session, 1);
        }
    }

    private String loadClassesPage(Model model, HttpSession session, int page) {
        Integer pageSize = (Integer) session.getAttribute("classPageSize");
        if (pageSize == null || pageSize <= 0) pageSize = 5;

        int firstResult = (page - 1) * pageSize;
        String campusId = staffsService.getCampus().getCampusId();

        List<MinorClasses> classes = classesService.getPaginatedClassesByCampus(firstResult, pageSize, campusId);
        long totalClasses = classesService.numberOfClassesByCampus(campusId);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));

        model.addAttribute("classes", classes);
        model.addAttribute("subjects", subjectsService.getAllSubjects());
        model.addAttribute("currentPageClasses", page);
        model.addAttribute("totalPagesClasses", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalClasses", totalPages);
        model.addAttribute("sessions", Sessions.values());
        model.addAttribute("currentCampusName", staffsService.getCampus().getCampusName());

        return "MinorClassesList";
    }

    @ModelAttribute("newClass")
    public MinorClasses populateNewClass() {
        return new MinorClasses();
    }
}