package com.example.demo.specializedClasses.controller;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.specializedClasses.service.SpecializedClassesService;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.staff.service.StaffsService;
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
@RequestMapping("/staff-home/specialized-classes-list")
public class ListSpecializedClassesController {

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializationService specializationService;

    @Autowired
    public ListSpecializedClassesController(SpecializedClassesService classesService, StaffsService staffsService, SpecializationService specializationService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.specializationService = specializationService;
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

            long totalClasses = classesService.numberOfClasses(staffsService.getStaff().getMajorManagement());
            int totalPagesClasses = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
            pageClasses = Math.max(1, Math.min(pageClasses, totalPagesClasses));
            session.setAttribute("currentPageClasses", pageClasses);
            session.setAttribute("totalPagesClasses", totalPagesClasses);

            int firstResult = (pageClasses - 1) * pageSize;
            List<SpecializedClasses> classes = classesService.getPaginatedClasses(firstResult, pageSize, staffsService.getStaff().getMajorManagement());

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPageClasses", 1);
                model.addAttribute("totalPagesClasses", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalClasses", 0);
                model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
                model.addAttribute("newClass", new SpecializedClasses());
                model.addAttribute("message", "No specialized classes found for this major.");
                model.addAttribute("alertClass", "alert-warning");
                return "SpecializedClassesList";
            }

            model.addAttribute("classes", classes);
            model.addAttribute("newClass", new SpecializedClasses());
            model.addAttribute("currentPageClasses", pageClasses);
            model.addAttribute("totalPagesClasses", totalPagesClasses);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", totalClasses);
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
            return "SpecializedClassesList";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while retrieving classes: " + e.getMessage()));
            model.addAttribute("newClass", new SpecializedClasses());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
            model.addAttribute("currentPageClasses", 1);
            model.addAttribute("totalPagesClasses", 1);
            model.addAttribute("pageSize", session.getAttribute("classPageSize") != null ? session.getAttribute("classPageSize") : 5);
            model.addAttribute("totalClasses", 0);
            return "SpecializedClassesList";
        }
    }
}