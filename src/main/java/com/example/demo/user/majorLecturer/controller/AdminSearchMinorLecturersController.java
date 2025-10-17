package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.user.majorLecturer.model.MinorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home/minor-lecturers-list/search-lecturers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSearchMinorLecturersController {

    private final MajorLecturersService lecturesService;
    private final AdminsService adminsService;

    public AdminSearchMinorLecturersController(MajorLecturersService lecturesService, AdminsService adminsService) {
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
                return "AdminSearchMinorLecturersList";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("minorLecturerPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            pageSize = Math.max(1, pageSize);
            session.setAttribute("minorLecturerPageSize", pageSize);
            session.setAttribute("minorLecturerPage", page);
            session.setAttribute("searchType", searchType);
            session.setAttribute("keyword", keyword);

            List<MinorLecturers> teachers;
            long totalLecturers;

            if (keyword == null || keyword.trim().isEmpty()) {
                totalLecturers = lecturesService.minorLecturersCountByCampus(campusId);
                teachers = lecturesService.getPaginatedMinorLecturersByCampus(campusId, (page - 1) * pageSize, pageSize);
            } else {
                teachers = lecturesService.searchMinorLecturersByCampus(campusId, searchType, keyword, (page - 1) * pageSize, pageSize);
                totalLecturers = lecturesService.countMinorLecturersSearchResultsByCampus(campusId, searchType, keyword);
            }

            int totalPages = Math.max(1, (int) Math.ceil((double) totalLecturers / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("minorLecturerTotalPages", totalPages);

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
            } else if (totalLecturers == 0 && keyword != null && !keyword.trim().isEmpty()) {
                model.addAttribute("error", "No minor lecturers found matching the search criteria.");
            }

            return "AdminSearchMinorLecturersList";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for minor lecturers: " + e.getMessage());
            model.addAttribute("teachers", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("searchType", searchType != null ? searchType : "name");
            model.addAttribute("keyword", keyword != null ? keyword : "");
            return "AdminSearchMinorLecturersList";
        }
    }

    @PostMapping
    public String searchMinorLecturers(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            String campusId = adminsService.getAdminCampus() != null ? adminsService.getAdminCampus().getCampusId() : null;
            if (campusId == null) {
                redirectAttributes.addFlashAttribute("error", "Admin campus not found.");
                return "redirect:/admin-home/minor-lecturers-list/search-lecturers";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("minorLecturerPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            pageSize = Math.max(1, pageSize);

            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("pageSize", pageSize);
            redirectAttributes.addAttribute("searchType", searchType);
            redirectAttributes.addAttribute("keyword", keyword);
            return "redirect:/admin-home/minor-lecturers-list/search-lecturers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while searching for minor lecturers: " + e.getMessage());
            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("pageSize", pageSize);
            redirectAttributes.addAttribute("searchType", searchType);
            redirectAttributes.addAttribute("keyword", keyword);
            return "redirect:/admin-home/minor-lecturers-list/search-lecturers";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getMinorLecturerAvatar(@PathVariable String id) {
        MinorLecturers lecturer = lecturesService.getMinorLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}