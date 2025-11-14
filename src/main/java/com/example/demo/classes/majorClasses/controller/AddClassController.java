package com.example.demo.classes.majorClasses.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/classes-list")
public class AddClassController {

    private final MajorClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    public AddClassController(MajorClassesService classesService,
                              StaffsService staffsService,
                              MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @PostMapping("/add-class")
    public String addClass(
            @Valid @ModelAttribute("newClass") MajorClasses newClass,
            BindingResult bindingResult,
            @RequestParam("subjectId") String subjectId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (subjectId != null && !subjectId.isBlank()) {
            newClass.setSubject(subjectsService.getSubjectById(subjectId));
        }

        Map<String, String> serviceErrors = classesService.validateClass(newClass, null);

        if (bindingResult.hasErrors() || !serviceErrors.isEmpty()) {
            model.addAttribute("org.springframework.validation.BindingResult.newClass", bindingResult);
            model.addAttribute("serviceErrors", serviceErrors);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("newClass", newClass);
            model.addAttribute("subjects", subjectsService.subjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("sessions", Sessions.values());
            return loadClassesPage(model, session, 1);
        }

        try {
            String majorId = staffsService.getStaffMajor() != null ? staffsService.getStaffMajor().getMajorId() : "default";
            String classId = classesService.generateUniqueClassId(majorId, LocalDateTime.now());
            newClass.setClassId(classId);
            newClass.setCreator(staffsService.getStaff());
            classesService.addClass(newClass);

            redirectAttributes.addFlashAttribute("successMessage", "Class added successfully!");
            return "redirect:/staff-home/classes-list";
        } catch (Exception e) {
            Map<String, String> errorMap = Map.of("general", "System error: " + e.getMessage());
            model.addAttribute("serviceErrors", errorMap);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("newClass", newClass);
            return loadClassesPage(model, session, 1);
        }
    }

    private String loadClassesPage(Model model, HttpSession session, int page) {
        Integer pageSize = (Integer) session.getAttribute("classPageSize");
        if (pageSize == null || pageSize <= 0) pageSize = 5;

        int firstResult = (page - 1) * pageSize;
        var staffMajor = staffsService.getStaffMajor();
        String campusId = staffsService.getCampusOfStaff().getCampusId();

        List<MajorClasses> classes = classesService.getPaginatedClassesByCampus(
                firstResult, pageSize, staffMajor, campusId);
        long totalClasses = classesService.numberOfClassesByCampus(staffMajor, campusId);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));

        model.addAttribute("classes", classes);
        model.addAttribute("subjects", subjectsService.subjectsByMajor(staffMajor));
        model.addAttribute("currentPageClasses", page);
        model.addAttribute("totalPagesClasses", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalClasses", totalClasses);
        model.addAttribute("sessions", Sessions.values());
        model.addAttribute("currentCampusName", staffsService.getCampusOfStaff().getCampusName());

        return "ClassesList";
    }

    @ModelAttribute("newClass")
    public MajorClasses populateNewClass() {
        return new MajorClasses();
    }
}