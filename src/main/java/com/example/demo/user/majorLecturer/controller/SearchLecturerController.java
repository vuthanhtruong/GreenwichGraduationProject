// src/main/java/com/example/demo/user/majorLecturer/controller/SearchLecturerController.java
package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/lecturers-list")
public class SearchLecturerController {

    private final MajorLecturersService lecturesService;
    private final StaffsService staffsService;

    public SearchLecturerController(MajorLecturersService lecturesService, StaffsService staffsService) {
        this.lecturesService = lecturesService;
        this.staffsService = staffsService;
    }

    @GetMapping("/search-lecturers")
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

    @PostMapping("/search-lecturers")
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
            return handleSearch(searchType, keyword, page, pageSize, successMessage, error, model, session, redirectAttributes);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Search failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("searchType", searchType);
            redirectAttributes.addFlashAttribute("keyword", keyword);
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize != null ? pageSize : 20);
            return "redirect:/staff-home/lecturers-list/search-lecturers";
        }
    }

    private String handleSearch(
            String searchType, String keyword, int page, Integer pageSize,
            String successMessage, String error,
            Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        String campusId = staffsService.getCampusOfStaff().getCampusId();
        String majorId = staffsService.getStaffMajor().getMajorId();
        String campusName = staffsService.getCampusOfStaff().getCampusName();
        String majorName = staffsService.getStaffMajor().getMajorName();

        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("lecturerSearchPageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 20;
        }
        session.setAttribute("lecturerSearchPageSize", pageSize);

        List<MajorLecturers> lecturers;
        long totalLecturers;
        int firstResult = (page - 1) * pageSize;

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                totalLecturers = lecturesService.totalLecturersByCampusAndMajor(campusId, majorId);
                lecturers = lecturesService.getPaginatedLecturersByCampusAndMajor(campusId, majorId, firstResult, pageSize);
            } else {
                searchType = (searchType == null || searchType.isBlank()) ? "name" : searchType;
                totalLecturers = lecturesService.countSearchLecturersByCampusAndMajor(campusId, majorId, searchType, keyword.trim());
                lecturers = lecturesService.searchLecturersByCampusAndMajor(campusId, majorId, searchType, keyword.trim(), firstResult, pageSize);
            }

            int totalPages = totalLecturers == 0 ? 1 : (int) Math.ceil((double) totalLecturers / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            model.addAttribute("teachers", lecturers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", totalLecturers);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
            model.addAttribute("currentCampusName", campusName);
            model.addAttribute("currentMajorName", majorName);

            if (successMessage != null) {
                model.addAttribute("message", successMessage);
            } else if (error != null) {
                model.addAttribute("error", error);
            } else if (totalLecturers == 0) {
                model.addAttribute("message", "No lecturers found in " + majorName + " matching your search.");
            }

            return "SearchLecturers";

        } catch (Exception e) {
            String errMsg = "Search error: " + e.getMessage();
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute("error", errMsg);
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/lecturers-list/search-lecturers";
            } else {
                model.addAttribute("error", errMsg);
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("searchType", "name");
                model.addAttribute("keyword", "");
                model.addAttribute("currentCampusName", campusName);
                model.addAttribute("currentMajorName", majorName);
                return "SearchLecturers";
            }
        }
    }
}