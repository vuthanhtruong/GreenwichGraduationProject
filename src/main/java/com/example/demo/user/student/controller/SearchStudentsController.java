// src/main/java/com/example/demo/user/student/controller/SearchStudentsController.java
package com.example.demo.user.student.controller;

import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
public class SearchStudentsController {

    private final StudentsService studentsService;
    private final StaffsService staffsService;

    public SearchStudentsController(StudentsService studentsService, StaffsService staffsService) {
        this.studentsService = studentsService;
        this.staffsService = staffsService;
    }

    @GetMapping("/search-students")
    public String showSearchPage(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(value = "successMessage", required = false) String successMessage,
            @RequestParam(value = "error", required = false) String error,
            Model model,
            HttpSession session) {

        return handleSearch(searchType, keyword, page, pageSize, successMessage, error, model, session, null);
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
            return handleSearch(searchType, keyword, page, pageSize, successMessage, error, model, session, redirectAttributes);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Search failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 20);
            return "redirect:/staff-home/search-students";
        }
    }

    private String handleSearch(
            String searchType, String keyword, int page, Integer pageSize,
            String successMessage, String error,
            Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        String campusId = staffsService.getCampusOfStaff().getCampusId();
        String majorId = staffsService.getStaff().getMajorManagement().getMajorId(); // Fixed: Only staff's major
        String campusName = staffsService.getCampusOfStaff().getCampusName();
        String majorName = staffsService.getStaffMajor().getMajorName();

        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("pageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 20;
        }
        session.setAttribute("pageSize", pageSize);

        List<Students> students;
        long totalStudents;

        int firstResult = (page - 1) * pageSize;

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                totalStudents = studentsService.totalStudentsByCampusAndMajor(campusId, majorId);
                students = studentsService.getPaginatedStudentsByCampusAndMajor(campusId, majorId, firstResult, pageSize);
            } else {
                searchType = (searchType == null || searchType.isBlank()) ? "name" : searchType;
                totalStudents = studentsService.countSearchResultsByCampusAndMajor(campusId, majorId, searchType, keyword.trim());
                students = studentsService.searchStudentsByCampusAndMajor(campusId, majorId, searchType, keyword.trim(), firstResult, pageSize);
            }

            int totalPages = totalStudents == 0 ? 1 : (int) Math.ceil((double) totalStudents / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
            model.addAttribute("currentCampusName", campusName);
            model.addAttribute("currentMajorName", majorName);

            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            } else if (totalStudents == 0) {
                model.addAttribute("message", "No students found in " + majorName + " matching your search.");
            }

            return "SearchStudents";

        } catch (Exception e) {
            String errMsg = "Search error: " + e.getMessage();
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute("error", errMsg);
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/search-students";
            } else {
                model.addAttribute("error", errMsg);
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", "name");
                model.addAttribute("keyword", "");
                model.addAttribute("currentCampusName", campusName);
                model.addAttribute("currentMajorName", majorName);
                return "SearchStudents";
            }
        }
    }
}