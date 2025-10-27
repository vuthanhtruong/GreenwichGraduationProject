package com.example.demo.user.minorLecturer.controller;

import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-lecturers-list")
public class ListMinorLecturersController {

    private final MinorLecturersService minorLecturersService;

    public ListMinorLecturersController(DeputyStaffsService deputyStaffsService, MinorLecturersService minorLecturersService) {
        this.minorLecturersService = minorLecturersService;
    }

    @GetMapping("")
    public String listMinorLecturers(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("minorLecturerPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("minorLecturerPageSize", pageSize);

            Long totalMinorLecturers = minorLecturersService.numberOfMinorLecturers();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalMinorLecturers / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("minorLecturerPage", page);
            session.setAttribute("minorLecturerTotalPages", totalPages);

            if (totalMinorLecturers == 0) {
                model.addAttribute("minorLecturers", new ArrayList<>());
                model.addAttribute("minorLecturer", new MinorLecturers());
                model.addAttribute("editMinorLecturer", new MinorLecturers());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalMinorLecturers", 0);
                model.addAttribute("alertClass", "alert-warning");
                return "MinorLecturersList";
            }

            int firstResult = (page - 1) * pageSize;
            List<MinorLecturers> minorLecturers = minorLecturersService.getPaginatedMinorLecturers(firstResult, pageSize);

            model.addAttribute("minorLecturers", minorLecturers);
            model.addAttribute("minorLecturer", new MinorLecturers());
            model.addAttribute("editMinorLecturer", new MinorLecturers());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalMinorLecturers", totalMinorLecturers);
            return "MinorLecturersList";
        } catch (SecurityException e) {
            model.addAttribute("errors", List.of("Security error: " + e.getMessage()));
            model.addAttribute("minorLecturer", new MinorLecturers());
            model.addAttribute("editMinorLecturer", new MinorLecturers());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalMinorLecturers", 0);
            return "MinorLecturersList";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getMinorLecturerAvatar(@PathVariable String id) {
        MinorLecturers minorLecturer = minorLecturersService.getMinorLecturerById(id);
        if (minorLecturer != null && minorLecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(minorLecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}