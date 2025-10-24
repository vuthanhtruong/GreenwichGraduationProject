 package com.example.demo.classes.specializedClasses.controller;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.specialization.service.SpecializationService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
public class SearchSpecializedClassesController {

    private final SpecializedClassesService classesService;
    private final StaffsService staffsService;
    private final SpecializationService specializationService;

    @Autowired
    public SearchSpecializedClassesController(SpecializedClassesService classesService, StaffsService staffsService, SpecializationService specializationService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.specializationService = specializationService;
    }

    @GetMapping("/search-classes")
    public String showSearchClasses(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
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

            List<SpecializedClasses> classes;
            long totalClasses;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalClasses = classesService.numberOfClasses(staffsService.getStaff().getMajorManagement());
                classes = classesService.getPaginatedClasses((page - 1) * pageSize, pageSize, staffsService.getStaff().getMajorManagement());
            } else {
                classes = classesService.searchClasses(searchType, keyword, (page - 1) * pageSize, pageSize, staffsService.getStaff().getMajorManagement());
                totalClasses = classesService.countSearchResults(searchType, keyword, staffsService.getStaff().getMajorManagement());
            }

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));
                model.addAttribute("message", "No classes found matching the search criteria.");
                return "SearchSpecializedClasses";
            }

            int totalPages = (int) Math.ceil((double) totalClasses / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("classes", classes);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaff().getMajorManagement()));

            return "SearchSpecializedClasses";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for classes: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/search-classes")
    public String searchClasses(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {
        return showSearchClasses(searchType, keyword, page, pageSize, model, session);
    }
}