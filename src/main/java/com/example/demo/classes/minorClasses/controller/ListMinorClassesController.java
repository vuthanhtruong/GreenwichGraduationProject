package com.example.demo.classes.minorClasses.controller;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.Sessions;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class ListMinorClassesController {

    private final MinorClassesService classesService;
    private final DeputyStaffsService deputyStaffsService;
    private final MinorSubjectsService subjectsService;

    @Autowired
    public ListMinorClassesController(MinorClassesService classesService, DeputyStaffsService deputyStaffsService, MinorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.deputyStaffsService = deputyStaffsService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("")
    public String showClassesList(
            @RequestParam(defaultValue = "1") int pageClasses,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session,
            Authentication authentication) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("classPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("classPageSize", pageSize);

            DeputyStaffs deputyStaff = deputyStaffsService.getDeputyStaff();
            if (deputyStaff == null) {
                model.addAttribute("errors", List.of("No authenticated deputy staff found."));
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPageClasses", 1);
                model.addAttribute("totalPagesClasses", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalClasses", 0);
                model.addAttribute("newClass", new MinorClasses());
                model.addAttribute("subjects", subjectsService.getAllSubjects());
                model.addAttribute("sessions", Sessions.values());
                return "MinorClassesList";
            }

            long totalClasses = classesService.numberOfClasses();
            int totalPagesClasses = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
            pageClasses = Math.max(1, Math.min(pageClasses, totalPagesClasses));
            session.setAttribute("currentPageClasses", pageClasses);
            session.setAttribute("totalPagesClasses", totalPagesClasses);

            int firstResult = (pageClasses - 1) * pageSize;
            List<MinorClasses> classes = classesService.getPaginatedClasses(firstResult, pageSize);

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPageClasses", 1);
                model.addAttribute("totalPagesClasses", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalClasses", 0);
                model.addAttribute("subjects", subjectsService.getAllSubjects());
                model.addAttribute("newClass", new MinorClasses());
                model.addAttribute("sessions", Sessions.values());
                model.addAttribute("message", "No classes found.");
                model.addAttribute("alertClass", "alert-warning");
                return "MinorClassesList";
            }

            model.addAttribute("classes", classes);
            model.addAttribute("newClass", new MinorClasses());
            model.addAttribute("currentPageClasses", pageClasses);
            model.addAttribute("totalPagesClasses", totalPagesClasses);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", totalClasses);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("sessions", Sessions.values());
            return "MinorClassesList";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while retrieving classes: " + e.getMessage()));
            model.addAttribute("classes", new ArrayList<>());
            model.addAttribute("currentPageClasses", 1);
            model.addAttribute("totalPagesClasses", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", 0);
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("newClass", new MinorClasses());
            model.addAttribute("sessions", Sessions.values());
            return "MinorClassesList";
        }
    }
}