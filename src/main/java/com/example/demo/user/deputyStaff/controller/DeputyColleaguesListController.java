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

    /* ====================== LIST + SEARCH + PAGE SIZE ====================== */
    @GetMapping("")
    public String listColleagues(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // --- PAGE SIZE ---
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("dsColleaguePageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 20;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("dsColleaguePageSize", pageSize);

        // --- LẤY TỪ SESSION (do POST) ---
        String keyword = (String) session.getAttribute("dsColleagueKeyword");
        String searchType = (String) session.getAttribute("dsColleagueSearchType");

        String campusId = staffsService.getCampusOfStaff().getCampusId();

        List<Staffs> colleagues;
        long totalColleagues;
        int totalPages;
        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            colleagues = staffsService.searchStaffsByCampus(campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalColleagues = staffsService.countSearchResultsByCampus(campusId, searchType, keyword.trim());
        } else {
            List<Staffs> all = staffsService.colleagueBycampusId(campusId);
            totalColleagues = all.size();
            int from = Math.min(firstResult, all.size());
            int to = Math.min(from + pageSize, all.size());
            colleagues = from < to ? all.subList(from, to) : new ArrayList<>();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("dsColleaguePage", page);
        session.setAttribute("dsColleagueTotalPages", totalPages);

        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", totalColleagues);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "DeputyColleaguesList";
    }

    /* ====================== SEARCH (POST) ====================== */
    @PostMapping("/search")
    public String searchColleagues(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpSession session) {

        session.setAttribute("dsColleagueKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("dsColleagueSearchType", searchType);
        session.setAttribute("dsColleaguePageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("dsColleaguePage", 1);

        return "redirect:/deputy-staff-home/colleagues-list";
    }

    /* ====================== CLEAR SEARCH ====================== */
    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("dsColleagueKeyword");
        session.removeAttribute("dsColleagueSearchType");
        session.setAttribute("dsColleaguePage", 1);
        return "redirect:/deputy-staff-home/colleagues-list";
    }

    /* ====================== CHANGE PAGE SIZE ====================== */
    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        pageSize = Math.max(1, Math.min(pageSize, 100));
        session.setAttribute("dsColleaguePageSize", pageSize);
        session.setAttribute("dsColleaguePage", 1);
        return "redirect:/deputy-staff-home/colleagues-list";
    }

    /* ====================== AVATAR ====================== */
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