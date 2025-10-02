package com.example.demo.student.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/admin-home/students-list/search-students")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSearchStudentsController {

    private final StudentsService studentsService;
    private final AdminsService adminsService;

    public AdminSearchStudentsController(StudentsService studentsService, AdminsService adminsService) {
        this.studentsService = studentsService;
        this.adminsService = adminsService;
    }

    @GetMapping
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
            String campusId = adminsService.getAdminCampus() != null ? adminsService.getAdminCampus().getCampusId() : null;
            if (campusId == null) {
                model.addAttribute("error", "Admin campus not found.");
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 5);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("campusId", campusId);
                return "AdminSearchStudents";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("studentPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            pageSize = Math.max(1, pageSize);
            session.setAttribute("studentPageSize", pageSize);
            session.setAttribute("studentPage", page);

            List<Students> students;
            long totalStudents;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalStudents = studentsService.totalStudentsByCampus(campusId);
                students = studentsService.getPaginatedStudentsByCampus(campusId, (page - 1) * pageSize, pageSize);
            } else {
                students = studentsService.searchStudentsByCampus(campusId, searchType, keyword, (page - 1) * pageSize, pageSize);
                totalStudents = studentsService.countSearchResultsByCampus(campusId, searchType, keyword);
            }

            int totalPages = Math.max(1, (int) Math.ceil((double) totalStudents / pageSize));
            page = Math.max(1, Math.min(page, totalPages));

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("campusId", campusId);
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "AdminSearchStudents";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for students: " + e.getMessage());
            model.addAttribute("students", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("campusId", adminsService.getAdmin().getCampus().getCampusId());
            return "AdminSearchStudents";
        }
    }

    @PostMapping
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
            String campusId = adminsService.getAdminCampus() != null ? adminsService.getAdminCampus().getCampusId() : null;
            if (campusId == null) {
                redirectAttributes.addFlashAttribute("error", "Admin campus not found.");
                return "redirect:/admin-home/students-list/search-students";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("studentPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            pageSize = Math.max(1, pageSize);
            session.setAttribute("studentPageSize", pageSize);
            session.setAttribute("studentPage", page);

            List<Students> students;
            long totalStudents;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalStudents = studentsService.totalStudentsByCampus(campusId);
                students = studentsService.getPaginatedStudentsByCampus(campusId, (page - 1) * pageSize, pageSize);
            } else {
                students = studentsService.searchStudentsByCampus(campusId, searchType, keyword, (page - 1) * pageSize, pageSize);
                totalStudents = studentsService.countSearchResultsByCampus(campusId, searchType, keyword);
            }

            int totalPages = Math.max(1, (int) Math.ceil((double) totalStudents / pageSize));
            page = Math.max(1, Math.min(page, totalPages));

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("campusId", campusId);
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            }

            return "AdminSearchStudents";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while searching for students: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/admin-home/students-list/search-students";
        }
    }
}