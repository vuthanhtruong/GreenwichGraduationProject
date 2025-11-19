package com.example.demo.user.minorLecturer.controller;

import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-lecturers-list")
public class SearchMinorLecturersController {

    private final MinorLecturersService service;
    private final DeputyStaffsService deputyStaffsService;

    public SearchMinorLecturersController(MinorLecturersService service, DeputyStaffsService deputyStaffsService) {
        this.service = service;
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping("/search-minor-lecturers")
    public String search(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {

        return doSearch(searchType, keyword, page, pageSize, model, session);
    }

    @PostMapping("/search-minor-lecturers")
    public String searchPost(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            Model model,
            HttpSession session) {

        return doSearch(searchType, keyword, page, pageSize, model, session);
    }

    private String doSearch(String searchType, String keyword, int page, Integer pageSize,
                            Model model, HttpSession session) {

        // Default values
        if (pageSize == null || pageSize < 1) {
            pageSize = (Integer) session.getAttribute("minorLecturerPageSize");
            if (pageSize == null || pageSize < 1) pageSize = 20;
        }
        session.setAttribute("minorLecturerPageSize", pageSize);

        if (searchType == null || searchType.isBlank()) searchType = "name";
        if (keyword != null) keyword = keyword.trim();
        String campusId = deputyStaffsService.getCampus().getCampusId();

        List<MinorLecturers> lecturers;
        long totalLecturers;
        int totalPages;

        if (keyword == null || keyword.isEmpty()) {
            // Danh sách thường (không search)
            totalLecturers = service.numberOfMinorLecturers(); // hoặc có thể dùng count by campus nếu muốn
            lecturers = service.getPaginatedMinorLecturers((page - 1) * pageSize, pageSize);
            totalPages = (int) Math.ceil((double) totalLecturers / pageSize);
        } else {
            // Tìm kiếm theo campus của deputy staff
            lecturers = service.searchMinorLecturersByCampus(
                    campusId, searchType, keyword, (page - 1) * pageSize, pageSize);
            totalLecturers = service.countSearchMinorLecturersByCampus(campusId, searchType, keyword);
            totalPages = (int) Math.ceil((double) totalLecturers / pageSize);
        }

        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        model.addAttribute("minorLecturers", lecturers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalMinorLecturers", totalLecturers);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentCampusName", deputyStaffsService.getCampus().getCampusName());

        // Để overlay add/edit vẫn hoạt động
        model.addAttribute("minorLecturer", new MinorLecturers());
        model.addAttribute("editMinorLecturer", new MinorLecturers());

        return "SearchMinorLecturers"; // tên file HTML bên dưới
    }
}