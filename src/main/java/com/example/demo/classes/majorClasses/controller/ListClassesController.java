package com.example.demo.classes.majorClasses.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class ListClassesController {

    private final MajorClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public ListClassesController(MajorClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
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

            String majorId = staffsService.getStaffMajor() != null ? staffsService.getStaffMajor().getMajorId() : "default";
            long totalClasses = classesService.numberOfClassesByCampus(staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
            int totalPagesClasses = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
            pageClasses = Math.max(1, Math.min(pageClasses, totalPagesClasses));
            session.setAttribute("currentPageClasses", pageClasses);
            session.setAttribute("totalPagesClasses", totalPagesClasses);

            int firstResult = (pageClasses - 1) * pageSize;
            List<MajorClasses> classes = classesService.getPaginatedClassesByCampus(firstResult, pageSize, staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPageClasses", 1);
                model.addAttribute("totalPagesClasses", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalClasses", 0);
                model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
                model.addAttribute("newClass", new MajorClasses());
                model.addAttribute("message", "No classes found for this major.");
                model.addAttribute("alertClass", "alert-warning");
                return "ClassesList";
            }

            model.addAttribute("classes", classes);
            model.addAttribute("newClass", new MajorClasses());
            model.addAttribute("currentPageClasses", pageClasses);
            model.addAttribute("totalPagesClasses", totalPagesClasses);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", totalClasses);
            model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
            return "ClassesList";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while retrieving classes: " + e.getMessage()));
            model.addAttribute("newClass", new MajorClasses());
            model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("currentPageClasses", 1);
            model.addAttribute("totalPagesClasses", 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", 0);
            return "ClassesList";
        }
    }
}