package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
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
@RequestMapping("/admin-home/lecturers-list/search-lecturers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSearchLecturersController {

    private final MajorLecturersService lecturesService;
    private final AdminsService adminsService;

    public AdminSearchLecturersController(MajorLecturersService lecturesService, AdminsService adminsService) {
        this.lecturesService = lecturesService;
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
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 5);
                model.addAttribute("totalLecturers", 0);
                model.addAttribute("searchType", searchType != null ? searchType : "name");
                model.addAttribute("keyword", keyword != null ? keyword : "");
                model.addAttribute("campusId", campusId);
                return "AdminSearchLecturersList";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("lecturerPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            pageSize = Math.max(1, pageSize);
            session.setAttribute("lecturerPageSize", pageSize);
            session.setAttribute("lecturerPage", page);

            List<MajorLecturers> teachers;
            long totalLecturers;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalLecturers = lecturesService.countLecturersByCampus(campusId);
                teachers = lecturesService.getPaginatedLecturersByCampus(campusId, (page - 1) * pageSize, pageSize);
            } else {
                teachers = lecturesService.searchMajorLecturersByCampus(campusId, searchType, keyword, (page - 1) * pageSize, pageSize);
                totalLecturers = lecturesService.countMajorLecturersSearchResultsByCampus(campusId, searchType, keyword);
            }

            int totalPages = Math.max(1, (int) Math.ceil((double) totalLecturers / pageSize));
            page = Math.max(1, Math.min(page, totalPages));

            model.addAttribute("teachers", teachers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", totalLecturers);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("campusId", campusId);
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            } else if (totalLecturers == 0) {
                model.addAttribute("error", "No lecturers found matching the search criteria.");
            }

            return "AdminSearchLecturersList";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for lecturers: " + e.getMessage());
            model.addAttribute("teachers", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            return "AdminSearchLecturersList";
        }
    }

    @PostMapping
    public String searchLecturers(
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
                return "redirect:/admin-home/lecturers-list/search-lecturers";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("lecturerPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            pageSize = Math.max(1, pageSize);
            session.setAttribute("lecturerPageSize", pageSize);
            session.setAttribute("lecturerPage", page);

            List<MajorLecturers> teachers;
            long totalLecturers;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalLecturers = lecturesService.countLecturersByCampus(campusId);
                teachers = lecturesService.getPaginatedLecturersByCampus(campusId, (page - 1) * pageSize, pageSize);
            } else {
                teachers = lecturesService.searchMajorLecturersByCampus(campusId, searchType, keyword, (page - 1) * pageSize, pageSize);
                totalLecturers = lecturesService.countMajorLecturersSearchResultsByCampus(campusId, searchType, keyword);
            }

            int totalPages = Math.max(1, (int) Math.ceil((double) totalLecturers / pageSize));
            page = Math.max(1, Math.min(page, totalPages));

            model.addAttribute("teachers", teachers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", totalLecturers);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("campusId", campusId);
            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            } else if (totalLecturers == 0) {
                model.addAttribute("error", "No lecturers found matching the search criteria.");
            }

            return "AdminSearchLecturersList";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while searching for lecturers: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("pageSize", pageSize);
            return "redirect:/admin-home/lecturers-list/search-lecturers";
        }
    }
}