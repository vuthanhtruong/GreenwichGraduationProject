package com.example.demo.student.controller;

import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
public class SearchStudentsController {

    private final StudentsService studentsService;

    public SearchStudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("/search-students")
    public String showSearchPage(
            Model model,
            HttpSession session,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            @RequestParam(value = "error", required = false) String error) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("pageSize", pageSize);

            List<Students> students;
            long totalStudents;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalStudents = studentsService.numberOfStudents();
                students = studentsService.getPaginatedStudents((page - 1) * pageSize, pageSize);
            } else {
                students = studentsService.searchStudents(searchType, keyword, (page - 1) * pageSize, pageSize);
                totalStudents = studentsService.countSearchResults(searchType, keyword);
            }

            if (totalStudents == 0) {
                model.addAttribute("student", new Students());
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No students found matching the search criteria."));
                return "SearchStudents";
            }

            int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "SearchStudents";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for students: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/search-students")
    public String searchStudents(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            @RequestParam(value = "error", required = false) String error,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            List<Students> students;
            long totalStudents;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalStudents = studentsService.numberOfStudents();
                students = studentsService.getPaginatedStudents((page - 1) * pageSize, pageSize);
            } else {
                students = studentsService.searchStudents(searchType, keyword, (page - 1) * pageSize, pageSize);
                totalStudents = studentsService.countSearchResults(searchType, keyword);
            }

            if (totalStudents == 0) {
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("message", successMessage != null ? successMessage : (error != null ? error : "No students found matching the search criteria."));
                return "SearchStudents";
            }

            int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "SearchStudents";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while searching for students: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/search-students";
        }
    }
}