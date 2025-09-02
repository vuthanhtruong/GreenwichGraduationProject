package com.example.demo.classes.controller;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public ListClassesController(ClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("")
    public String showClassesList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            long totalClasses = classesService.numberOfClasses(staffsService.getStaffMajor());
            List<MajorClasses> classes = classesService.getPaginatedClasses((page - 1) * pageSize, pageSize, staffsService.getStaffMajor());

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
                model.addAttribute("message", "No classes found for this major.");
                return "ClassesList";
            }

            int totalPages = (int) Math.ceil((double) totalClasses / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("classes", classes);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));

            return "ClassesList";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while retrieving classes: " + e.getMessage());
            return "error";
        }
    }
}