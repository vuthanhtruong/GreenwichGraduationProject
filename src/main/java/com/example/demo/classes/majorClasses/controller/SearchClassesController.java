package com.example.demo.classes.majorClasses.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class SearchClassesController {

    private final MajorClassesService classesService;
    private final StaffsService staffsService;
    private final MajorSubjectsService subjectsService;

    public SearchClassesController(MajorClassesService classesService, StaffsService staffsService, MajorSubjectsService subjectsService) {
        this.classesService = classesService;
        this.staffsService = staffsService;
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
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            List<MajorClasses> classes;
            long totalClasses;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalClasses = classesService.numberOfClassesByCampus(staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
                classes = classesService.getPaginatedClassesByCampus((page - 1) * pageSize, pageSize, staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
            } else {
                classes = classesService.searchClassesByCampus(searchType, keyword, (page - 1) * pageSize, pageSize, staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
                totalClasses = classesService.countSearchResultsByCampus(searchType, keyword, staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
            }

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
                model.addAttribute("message", "No classes found matching the search criteria.");
                return "SearchClasses";
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
            model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));

            return "SearchClasses";
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
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            List<MajorClasses> classes;
            long totalClasses;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalClasses = classesService.numberOfClassesByCampus(staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
                classes = classesService.getPaginatedClassesByCampus((page - 1) * pageSize, pageSize, staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
            } else {
                classes = classesService.searchClassesByCampus(searchType, keyword, (page - 1) * pageSize, pageSize, staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
                totalClasses = classesService.countSearchResultsByCampus(searchType, keyword, staffsService.getStaffMajor(),staffsService.getCampusOfStaff().getCampusId());
            }

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));
                model.addAttribute("message", "No classes found matching the search criteria.");
                return "SearchClasses";
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
            model.addAttribute("subjects", subjectsService.AcceptedSubjectsByMajor(staffsService.getStaffMajor()));

            return "SearchClasses";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for classes: " + e.getMessage());
            return "error";
        }
    }
}