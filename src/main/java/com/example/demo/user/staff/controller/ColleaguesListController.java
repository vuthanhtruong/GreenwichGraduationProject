package com.example.demo.user.staff.controller;

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
@RequestMapping("/staff-home/colleagues-list")
public class ColleaguesListController {

    private final StaffsService staffsService;

    public ColleaguesListController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    /* ====================== LIST + SEARCH + PAGE SIZE ====================== */
    @GetMapping("")
    public String listColleagues(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // ---- PAGE SIZE ----
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("colleaguePageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 10;
        }
        pageSize = Math.min(pageSize, 100); // giới hạn tối đa
        session.setAttribute("colleaguePageSize", pageSize);

        // ---- LẤY DỮ LIỆU TÌM KIẾM TỪ SESSION (do POST) ----
        String keyword = (String) session.getAttribute("colleagueKeyword");
        String searchType = (String) session.getAttribute("colleagueSearchType");

        String campusId = staffsService.getCampusOfStaff().getCampusId();

        List<Staffs> colleagues;
        long totalColleagues;
        int totalPages;

        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            // Tìm kiếm
            colleagues = staffsService.searchStaffsByCampus(campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalColleagues = staffsService.countSearchResultsByCampus(campusId, searchType, keyword.trim());
        } else {
            // Danh sách thường
            List<Staffs> all = staffsService.colleagueBycampusId(campusId);
            totalColleagues = all.size();
            colleagues = all.stream()
                    .skip(firstResult)
                    .limit(pageSize)
                    .toList();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        // Lưu page hiện tại
        session.setAttribute("colleaguePage", page);
        session.setAttribute("colleagueTotalPages", totalPages);

        // ---- MODEL ----
        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", totalColleagues);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("defaultAvatar", "/images/default-avatar.jpg"); // nếu cần

        if (totalColleagues == 0) {
            model.addAttribute("colleagues", new ArrayList<>());
        }

        return "StaffColleaguesList";
    }

    /* ====================== SEARCH (POST) ====================== */
    @PostMapping("/search")
    public String searchColleagues(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session,
            Model model) {

        // Lưu vào session
        session.setAttribute("colleagueKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("colleagueSearchType", searchType);
        session.setAttribute("colleaguePageSize", pageSize);
        session.setAttribute("colleaguePage", 1); // reset về trang 1

        // Redirect để tránh submit lại form
        return "redirect:/staff-home/colleagues-list";
    }

    /* ====================== CLEAR SEARCH ====================== */
    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("colleagueKeyword");
        session.removeAttribute("colleagueSearchType");
        session.setAttribute("colleaguePage", 1);
        return "redirect:/staff-home/colleagues-list";
    }

    /* ====================== CHANGE PAGE SIZE ====================== */
    @PostMapping("/change-page-size")
    public String changePageSize(
            @RequestParam int pageSize,
            HttpSession session) {
        pageSize = Math.max(1, Math.min(pageSize, 100));
        session.setAttribute("colleaguePageSize", pageSize);
        session.setAttribute("colleaguePage", 1);
        return "redirect:/staff-home/colleagues-list";
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