package com.example.demo.lecturer.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.lecturer.model.MinorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home/minor-lecturers-list")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMinorLecturersController {

    private final LecturesService lecturesService;
    private final AdminsService adminsService;

    public AdminMinorLecturersController(LecturesService lecturesService, AdminsService adminsService) {
        this.lecturesService = lecturesService;
        this.adminsService = adminsService;
    }

    @GetMapping("")
    public String listMinorLecturers(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            String campusId = adminsService.getAdminCampus() != null ? adminsService.getAdminCampus().getCampusId() : null;
            if (campusId == null) {
                model.addAttribute("error", "Admin campus not found.");
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 5);
                model.addAttribute("totalLecturers", 0);
                return "AdminMinorLecturersList";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("minorLecturerPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            pageSize = Math.max(1, pageSize);
            session.setAttribute("minorLecturerPageSize", pageSize);

            Long totalLecturers = lecturesService.minorLecturersCountByCampus(campusId);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalLecturers / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("minorLecturerPage", page);
            session.setAttribute("minorLecturerTotalPages", totalPages);

            if (totalLecturers == 0) {
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalLecturers", 0);
                model.addAttribute("message", "No minor lecturers found.");
                model.addAttribute("campusId", campusId);
                return "AdminMinorLecturersList";
            }

            int firstResult = (page - 1) * pageSize;
            List<MinorLecturers> teachers = lecturesService.getPaginatedMinorLecturersByCampus(campusId, firstResult, pageSize);

            model.addAttribute("teachers", teachers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", totalLecturers);
            model.addAttribute("campusId", campusId);
            return "AdminMinorLecturersList";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            model.addAttribute("teachers", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", 0);
            return "AdminMinorLecturersList";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getMinorLecturerAvatar(@PathVariable String id) {
        MinorLecturers lecturer = lecturesService.getMinorLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}