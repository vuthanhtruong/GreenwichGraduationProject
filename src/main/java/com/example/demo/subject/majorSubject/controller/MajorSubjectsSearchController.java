package com.example.demo.subject.majorSubject.controller;

import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
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
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class MajorSubjectsSearchController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;

    @Autowired
    public MajorSubjectsSearchController(MajorSubjectsService subjectsService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
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
                return "MajorSubjectsSearch";
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
                return "MajorSubjectsSearch";
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

            // Get the major for the authenticated staff
            Majors major = staffsService.getStaffMajor();
            if (major == null) {
                model.addAttribute("errors", List.of("No major found for the current staff."));
                model.addAttribute("subjects", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalSubjects", 0);
                model.addAttribute("searchType", searchType);
                model.addAttribute("keyword", keyword);
                return "MajorSubjectsSearch";
            }

            // Count total search results
            Long totalSubjects = subjectsService.countSearchResults(searchType, keyword, major);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("subjectPage", page);
            session.setAttribute("subjectTotalPages", totalPages);

            // Fetch paginated search results
            int firstResult = (page - 1) * pageSize;
            List<MajorSubjects> subjects = subjectsService.searchSubjects(searchType, keyword, firstResult, pageSize, major);

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

            return "MajorSubjectsSearch";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while searching subjects: " + e.getMessage()));
            model.addAttribute("subjects", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize != null && pageSize > 0 ? pageSize : 5);
            model.addAttribute("totalSubjects", 0);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            return "MajorSubjectsSearch";
        }
    }
}