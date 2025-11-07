package com.example.demo.user.deputyStaff.controller;

import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/colleagues-list")
public class DeputyColleaguesListController {

    private final StaffsService staffsService;

    public DeputyColleaguesListController(StaffsService staffsService) {
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

        // === PAGE SIZE ===
        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("deputyColleaguePageSize");
            if (pageSize == null) pageSize = 10;
        }
        session.setAttribute("deputyColleaguePageSize", pageSize);

        // === DATA SOURCE ===
        List<Staffs> colleagues;
        long totalColleagues;
        int totalPages;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            int firstResult = (page - 1) * pageSize;
            colleagues = staffsService.searchStaffsByCampus(campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalColleagues = staffsService.countSearchResultsByCampus(campusId, searchType, keyword.trim());
        } else {
            List<Staffs> all = staffsService.colleagueBycampusId(campusId);
            int from = (page - 1) * pageSize;
            int to = Math.min(from + pageSize, all.size());
            colleagues = from < all.size() ? all.subList(from, to) : new ArrayList<>();
            totalColleagues = all.size();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("deputyColleaguePage", page);
        session.setAttribute("deputyColleagueTotalPages", totalPages);

        // === MODEL ===
        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", totalColleagues);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "DeputyColleaguesList";
    }

    // === AVATAR ENDPOINT ===
    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getStaffAvatar(@PathVariable String id) {
        Staffs staff = staffsService.getStaffById(id);
        if (staff != null && staff.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(staff.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}