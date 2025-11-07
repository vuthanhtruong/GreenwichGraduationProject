package com.example.demo.user.staff.controller;

import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/colleagues-list")
public class ColleaguesListController {

    private final StaffsService staffsService;

    public ColleaguesListController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @GetMapping
    public String listColleagues(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {

        String campusId = staffsService.getCampusOfStaff().getCampusId();

        // === XỬ LÝ PAGE SIZE ===
        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("colleaguePageSize");
            if (pageSize == null) pageSize = 10;
        }
        session.setAttribute("colleaguePageSize", pageSize);

        // === TÌM KIẾM HOẶC LẤY TẤT CẢ ===
        List<Staffs> colleagues;
        long totalColleagues;
        int totalPages;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            int firstResult = (page - 1) * pageSize;
            colleagues = staffsService.searchStaffsByCampus(campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalColleagues = staffsService.countSearchResultsByCampus(campusId, searchType, keyword.trim());
        } else {
            int firstResult = (page - 1) * pageSize;
            colleagues = staffsService.colleagueBycampusId(campusId)
                    .stream()
                    .skip(firstResult)
                    .limit(pageSize)
                    .toList();
            totalColleagues = staffsService.colleagueBycampusId(campusId).size();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("colleaguePage", page);
        session.setAttribute("colleagueTotalPages", totalPages);

        // === TRUYỀN DỮ LIỆU ===
        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", totalColleagues);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        // Empty state
        if (totalColleagues == 0) {
            model.addAttribute("colleagues", new ArrayList<>());
        }

        return "StaffColleaguesList";
    }

    // === AVATAR ENDPOINT ===
    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getStaffAvatar(@PathVariable String id) {
        Staffs staff = staffsService.getStaffById(id);
        if (staff != null && staff.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.IMAGE_JPEG)
                    .body(staff.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}