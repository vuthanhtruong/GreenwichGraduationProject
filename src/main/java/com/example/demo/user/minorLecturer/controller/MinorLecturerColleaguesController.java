// File: MinorLecturerColleaguesController.java
package com.example.demo.user.minorLecturer.controller;

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
@RequestMapping("/minor-lecturer-home/your-colleagues")
public class MinorLecturerColleaguesController {

    private final MinorLecturersService minorLecturersService;

    public MinorLecturerColleaguesController(MinorLecturersService minorLecturersService) {
        this.minorLecturersService = minorLecturersService;
    }

    @GetMapping
    public String listColleagues(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // --- PAGE SIZE ---
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("minorColleaguePageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 20;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("minorColleaguePageSize", pageSize);

        // --- LẤY MINOR LECTURER HIỆN TẠI ---
        MinorLecturers currentLecturer = minorLecturersService.getMinorLecturer();
        if (currentLecturer == null || currentLecturer.getCampus() == null) {
            return "redirect:/login";
        }

        String campusId = currentLecturer.getCampus().getCampusId();

        // --- TÌM KIẾM TỪ SESSION ---
        String keyword = (String) session.getAttribute("minorColleagueKeyword");
        String searchType = (String) session.getAttribute("minorColleagueSearchType");

        List<MinorLecturers> colleagues;
        long totalColleagues;
        int totalPages;
        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            colleagues = minorLecturersService.searchMinorLecturersByCampus(
                    campusId, searchType, keyword.trim(), firstResult, pageSize);
            totalColleagues = minorLecturersService.countSearchMinorLecturersByCampus(
                    campusId, searchType, keyword.trim());
        } else {
            colleagues = minorLecturersService.colleaguesByCampusId(campusId);
            totalColleagues = colleagues.size();
            colleagues = colleagues.stream()
                    .skip(firstResult)
                    .limit(pageSize)
                    .toList();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("minorColleaguePage", page);
        session.setAttribute("minorColleagueTotalPages", totalPages);

        // --- MODEL ---
        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", totalColleagues);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "MinorLecturerColleagues";
    }

    @PostMapping("/search")
    public String search(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpSession session) {

        session.setAttribute("minorColleagueKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("minorColleagueSearchType", searchType);
        session.setAttribute("minorColleaguePageSize", Math.min(pageSize, 100));
        session.setAttribute("minorColleaguePage", 1);

        return "redirect:/minor-lecturer-home/your-colleagues";
    }

    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("minorColleagueKeyword");
        session.removeAttribute("minorColleagueSearchType");
        session.setAttribute("minorColleaguePage", 1);
        return "redirect:/minor-lecturer-home/your-colleagues";
    }

    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        session.setAttribute("minorColleaguePageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("minorColleaguePage", 1);
        return "redirect:/minor-lecturer-home/your-colleagues";
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) {
        MinorLecturers lecturer = minorLecturersService.getMinorLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}