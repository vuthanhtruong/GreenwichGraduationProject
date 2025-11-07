package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/major-lecturer-home/your-colleagues")
public class MajorLecturerColleaguesController {

    private final MajorLecturersService majorLecturersService;

    public MajorLecturerColleaguesController(MajorLecturersService majorLecturersService) {
        this.majorLecturersService = majorLecturersService;
    }

    @GetMapping("")
    public String listColleagues(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {

        // --- PAGE SIZE ---
        if (pageSize == null || pageSize <= 0) {
            pageSize = (Integer) session.getAttribute("mlColleaguePageSize");
            if (pageSize == null || pageSize <= 0) pageSize = 20;
        }
        pageSize = Math.min(pageSize, 100);
        session.setAttribute("mlColleaguePageSize", pageSize);

        // --- LẤY TỪ SESSION ---
        String keyword = (String) session.getAttribute("mlColleagueKeyword");
        String searchType = (String) session.getAttribute("mlColleagueSearchType");

        // --- LẤY MAJOR ID ---
        MajorLecturers currentLecturer = majorLecturersService.getMajorLecturer();
        String currentMajorId = currentLecturer.getMajorManagement().getMajorId();

        List<MajorLecturers> colleagues;
        long totalColleagues;
        int totalPages;
        int firstResult = (page - 1) * pageSize;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            // === TÌM KIẾM + LỌC THEO MAJOR ===
            List<MajorLecturers> searchResults = majorLecturersService.searchMajorLecturersByCampus(
                    currentLecturer.getCampus().getCampusId(), searchType, keyword.trim(), firstResult, pageSize);

            colleagues = searchResults.stream()
                    .filter(l -> l.getMajorManagement() != null && currentMajorId.equals(l.getMajorManagement().getMajorId()))
                    .toList();

            // Dùng countSearchMajorLecturersByCampus → trả về long → không stream
            long rawCount = majorLecturersService.countSearchMajorLecturersByCampus(
                    currentLecturer.getCampus().getCampusId(), searchType, keyword.trim());

            // Lọc lại count theo major
            List<MajorLecturers> allMatching = majorLecturersService.searchMajorLecturersByCampus(
                    currentLecturer.getCampus().getCampusId(), searchType, keyword.trim(), 0, Integer.MAX_VALUE);

            totalColleagues = allMatching.stream()
                    .filter(l -> l.getMajorManagement() != null && currentMajorId.equals(l.getMajorManagement().getMajorId()))
                    .count();

        } else {
            // === LẤY TẤT CẢ ĐỒNG NGHIỆP CÙNG MAJOR ===
            List<MajorLecturers> allColleagues = majorLecturersService.getColleaguesByMajor(currentMajorId);

            totalColleagues = allColleagues.size();
            int from = Math.min(firstResult, allColleagues.size());
            int to = Math.min(from + pageSize, allColleagues.size());
            colleagues = from < to ? allColleagues.subList(from, to) : new ArrayList<>();
        }

        totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("mlColleaguePage", page);
        session.setAttribute("mlColleagueTotalPages", totalPages);

        // --- MODEL ---
        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", totalColleagues);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "MajorLecturerColleagues";
    }

    /* ====================== SEARCH (POST) ====================== */
    @PostMapping("/search")
    public String searchColleagues(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpSession session) {

        session.setAttribute("mlColleagueKeyword", keyword.trim().isEmpty() ? null : keyword.trim());
        session.setAttribute("mlColleagueSearchType", searchType);
        session.setAttribute("mlColleaguePageSize", Math.max(1, Math.min(pageSize, 100)));
        session.setAttribute("mlColleaguePage", 1);

        return "redirect:/major-lecturer-home/your-colleagues";
    }

    /* ====================== CLEAR SEARCH ====================== */
    @GetMapping("/clear-search")
    public String clearSearch(HttpSession session) {
        session.removeAttribute("mlColleagueKeyword");
        session.removeAttribute("mlColleagueSearchType");
        session.setAttribute("mlColleaguePage", 1);
        return "redirect:/major-lecturer-home/your-colleagues";
    }

    /* ====================== CHANGE PAGE SIZE ====================== */
    @PostMapping("/change-page-size")
    public String changePageSize(@RequestParam int pageSize, HttpSession session) {
        pageSize = Math.max(1, Math.min(pageSize, 100));
        session.setAttribute("mlColleaguePageSize", pageSize);
        session.setAttribute("mlColleaguePage", 1);
        return "redirect:/major-lecturer-home/your-colleagues";
    }

    /* ====================== AVATAR ====================== */
    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getLecturerAvatar(@PathVariable String id) {
        MajorLecturers lecturer = majorLecturersService.getLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}