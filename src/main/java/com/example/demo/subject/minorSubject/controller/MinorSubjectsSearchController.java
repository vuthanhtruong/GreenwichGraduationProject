package com.example.demo.subject.minorSubject.controller;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-subjects-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorSubjectsSearchController {

    private final MinorSubjectsService subjectsService;
    private final DeputyStaffsService deputyStaffsService;

    @Autowired
    public MinorSubjectsSearchController(MinorSubjectsService subjectsService, DeputyStaffsService deputyStaffsService) {
        this.subjectsService = subjectsService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @PostMapping("/search-subjects")
    public String searchSubjects(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {
        try {
            // Validate inputs
            if (searchType == null || !List.of("name", "id").contains(searchType.trim().toLowerCase())) {
                model.addAttribute("errors", List.of("Invalid search type. Use 'name' or 'id'."));
                model.addAttribute("subjects", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize != null && pageSize > 0 ? pageSize : 5);
                model.addAttribute("totalSubjects", 0);
                model.addAttribute("searchType", searchType);
                model.addAttribute("keyword", keyword);
                return "MinorSubjectsSearch";
            }

            if (keyword == null || keyword.trim().isEmpty()) {
                model.addAttribute("errors", List.of("Search keyword cannot be empty."));
                model.addAttribute("subjects", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize != null && pageSize > 0 ? pageSize : 5);
                model.addAttribute("totalSubjects", 0);
                model.addAttribute("searchType", searchType);
                model.addAttribute("keyword", keyword);
                return "MinorSubjectsSearch";
            }

            // Validate and set pageSize from session or default to 5
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("subjectPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            if (pageSize <= 0) {
                pageSize = 5;
            }
            session.setAttribute("subjectPageSize", pageSize);

            // Count total search results
            Long totalSubjects = subjectsService.countSearchResults(searchType, keyword);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("subjectPage", page);
            session.setAttribute("subjectTotalPages", totalPages);

            // Fetch paginated search results
            int firstResult = (page - 1) * pageSize;
            List<MinorSubjects> subjects = subjectsService.searchSubjects(searchType, keyword, firstResult, pageSize);

            // Populate model attributes for the view
            model.addAttribute("subjects", subjects);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", totalSubjects);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);

            // Add message only if no subjects are found
            if (subjects.isEmpty()) {
                model.addAttribute("message", "No subjects found for the search criteria.");
                model.addAttribute("alertClass", "alert-warning");
            }

            return "MinorSubjectsSearch";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while searching subjects: " + e.getMessage()));
            model.addAttribute("subjects", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize != null && pageSize > 0 ? pageSize : 5);
            model.addAttribute("totalSubjects", 0);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            return "MinorSubjectsSearch";
        }
    }
}