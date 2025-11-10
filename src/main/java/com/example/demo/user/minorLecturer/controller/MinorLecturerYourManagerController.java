// File: MinorLecturerYourManagerController.java
package com.example.demo.user.minorLecturer.controller;

import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/minor-lecturer-home/your-supervisor")
public class MinorLecturerYourManagerController {

    private final DeputyStaffsService deputyStaffsService;
    private final MinorLecturersService minorLecturersService;

    public MinorLecturerYourManagerController(
            DeputyStaffsService deputyStaffsService,
            MinorLecturersService minorLecturersService) {
        this.deputyStaffsService = deputyStaffsService;
        this.minorLecturersService = minorLecturersService;
    }

    @GetMapping
    public String listSupervisors(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // --- PAGE SIZE ---
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("minorSupervisorPageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 10;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("minorSupervisorPageSize", pageSize);

        // --- LẤY MINOR LECTURER HIỆN TẠI ---
        MinorLecturers currentLecturer = minorLecturersService.getMinorLecturer();
        if (currentLecturer == null || currentLecturer.getCampus() == null) {
            return "redirect:/login";
        }

        String campusId = currentLecturer.getCampus().getCampusId();

        // --- TÌM KIẾM TỪ SESSION ---
        String keyword = (String) session.getAttribute("minorSupervisorKeyword");
        String searchType = (String) session.getAttribute("minorSupervisorSearchType");

        List<DeputyStaffs> supervisors;
        long totalSupervisors;
        int totalPages;
        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            supervisors = deputyStaffsService.searchStaffsByCampus(
                    campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalSupervisors = deputyStaffsService.countSearchResultsByCampus(
                    campusId, searchType, keyword.trim());
        } else {
            supervisors = deputyStaffsService.getDeputyStaffsByCampus(campusId); // MỚI
            totalSupervisors = supervisors.size();
            supervisors = supervisors.stream()
                    .skip(firstResult)
                    .limit(pageSize)
                    .toList();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalSupervisors / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("minorSupervisorPage", page);
        session.setAttribute("minorSupervisorTotalPages", totalPages);

        // --- MODEL ---
        model.addAttribute("supervisors", supervisors);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalSupervisors", totalSupervisors);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("currentLecturer", currentLecturer);

        return "MinorLecturerYourManagerList";
    }

    @PostMapping("/search")
    public String search(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        session.setAttribute("minorSupervisorKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("minorSupervisorSearchType", searchType);
        session.setAttribute("minorSupervisorPageSize", Math.min(pageSize, 100));
        session.setAttribute("minorSupervisorPage", 1);

        return "redirect:/minor-lecturer-home/your-supervisor";
    }

    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("minorSupervisorKeyword");
        session.removeAttribute("minorSupervisorSearchType");
        session.setAttribute("minorSupervisorPage", 1);
        return "redirect:/minor-lecturer-home/your-supervisor";
    }

    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        session.setAttribute("minorSupervisorPageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("minorSupervisorPage", 1);
        return "redirect:/minor-lecturer-home/your-supervisor";
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) {
        DeputyStaffs deputy = deputyStaffsService.getDeputyStaffById(id);
        if (deputy != null && deputy.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(deputy.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}