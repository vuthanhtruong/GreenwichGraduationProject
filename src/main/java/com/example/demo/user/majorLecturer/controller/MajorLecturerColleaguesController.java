package com.example.demo.user.majorLecturer.controller;

import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
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
@RequestMapping("/major-lecturer-home/your-colleagues")
public class MajorLecturerColleaguesController {

    private final MajorLecturersService majorLecturersService;

    public MajorLecturerColleaguesController(MajorLecturersService majorLecturersService) {
        this.majorLecturersService = majorLecturersService;
    }

    @GetMapping
    public String listColleagues(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {

        String campusId = majorLecturersService.getMajorLecturer().getCampus().getCampusId();
        String currentMajorId = majorLecturersService.getMajorLecturer().getMajorManagement().getMajorId();

        // === PAGE SIZE ===
        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("lecturerColleaguePageSize");
            if (pageSize == null) pageSize = 10;
        }
        session.setAttribute("lecturerColleaguePageSize", pageSize);

        // === DATA ===
        List<MajorLecturers> colleagues;
        long totalColleagues;

        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            int firstResult = (page - 1) * pageSize;
            colleagues = majorLecturersService.searchMajorLecturersByCampus(
                            campusId, searchType, keyword.trim(), firstResult, pageSize)
                    .stream()
                    .filter(l -> l.getMajorManagement() != null && currentMajorId.equals(l.getMajorManagement().getMajorId()))
                    .toList();
            totalColleagues = colleagues.size(); // vì đã filter
        } else {
            List<MajorLecturers> all = majorLecturersService.colleagueBycampusId(campusId);
            List<MajorLecturers> filtered = all.stream()
                    .filter(l -> l.getMajorManagement() != null && currentMajorId.equals(l.getMajorManagement().getMajorId()))
                    .toList();

            int from = (page - 1) * pageSize;
            int to = Math.min(from + pageSize, filtered.size());
            colleagues = from < filtered.size() ? filtered.subList(from, to) : new ArrayList<>();
            totalColleagues = filtered.size();
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) totalColleagues / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        session.setAttribute("lecturerColleaguePage", page);
        session.setAttribute("lecturerColleagueTotalPages", totalPages);

        model.addAttribute("colleagues", colleagues);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalColleagues", totalColleagues);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "MajorLecturerColleagues";
    }

    // === AVATAR ENDPOINT ===
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