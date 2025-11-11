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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class SearchMinorClassesController {

    private final MinorClassesService classesService;
    private final DeputyStaffsService deputyStaffsService;
    private final MinorSubjectsService subjectsService;

    @Autowired
    public SearchMinorClassesController(MinorClassesService classesService, DeputyStaffsService deputyStaffsService, MinorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.deputyStaffsService = deputyStaffsService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("/search-classes")
    public String showSearchClasses(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {
        return handleSearchClasses(searchType, keyword, page, pageSize, model, session);
    }

    @PostMapping("/search-classes")
    public String searchClasses(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {
        return handleSearchClasses(searchType, keyword, page, pageSize, model, session);
    }

    private String handleSearchClasses(String searchType, String keyword, int page, Integer pageSize, Model model, HttpSession session) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            DeputyStaffs deputyStaff = deputyStaffsService.getDeputyStaff();
            if (deputyStaff == null) {
                model.addAttribute("errors", List.of("No authenticated deputy staff found."));
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("subjects", subjectsService.getAllSubjects());
                // Inside showClassesList(), after retrieving classes
                model.addAttribute("currentCampusName", deputyStaffsService.getCampus().getCampusName());
                return "SearchMinorClasses";
            }

            List<MinorClasses> classes;
            long totalClasses;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalClasses = classesService.numberOfClassesByCampus(deputyStaffsService.getCampus().getCampusId());
                classes = classesService.getPaginatedClassesByCampus((page - 1) * pageSize, pageSize,deputyStaffsService.getCampus().getCampusId());
            } else {
                classes = classesService.searchClassesByCampus(searchType, keyword, (page - 1) * pageSize, pageSize,deputyStaffsService.getCampus().getCampusId());
                totalClasses = classesService.countSearchResultsByCampus(searchType, keyword,deputyStaffsService.getCampus().getCampusId());
            }

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("subjects", subjectsService.getAllSubjects());
                model.addAttribute("message", "No classes found matching the search criteria.");
                model.addAttribute("currentCampusName", deputyStaffsService.getCampus().getCampusName());
                return "SearchMinorClasses";
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
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("currentCampusName", deputyStaffsService.getCampus().getCampusName());
            return "SearchMinorClasses";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while searching for classes: " + e.getMessage()));
            model.addAttribute("classes", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("subjects", subjectsService.getAllSubjects());
            model.addAttribute("currentCampusName", deputyStaffsService.getCampus().getCampusName());
            return "SearchMinorClasses";
        }
    }
}