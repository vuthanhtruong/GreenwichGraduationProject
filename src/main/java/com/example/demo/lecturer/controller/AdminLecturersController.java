package com.example.demo.lecturer.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin-home/lecturers-list")
public class AdminLecturersController {

    private final LecturesService lecturesService;
    private final AdminsService  adminsService;

    public AdminLecturersController(LecturesService lecturesService, AdminsService adminsService) {
        this.lecturesService = lecturesService;
        this.adminsService = adminsService;
    }

    @GetMapping("")
    public String listLecturers(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) { // Assume campusId is provided
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("lecturerPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("lecturerPageSize", pageSize);
            String campusId=adminsService.getAdminCampus().getCampusId();
            Long totalLecturers = lecturesService.countLecturersByCampus(campusId);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalLecturers / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("lecturerPage", page);
            session.setAttribute("lecturerTotalPages", totalPages);

            if (totalLecturers == 0) {
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalLecturers", 0);
                model.addAttribute("message", "No lecturers found.");
                model.addAttribute("campusId", campusId);
                return "AdminLecturersList";
            }

            int firstResult = (page - 1) * pageSize;
            List<MajorLecturers> teachers = lecturesService.getPaginatedLecturersByCampus(campusId, firstResult, pageSize);

            model.addAttribute("teachers", teachers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", totalLecturers);
            model.addAttribute("campusId", campusId);
            return "AdminLecturersList";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            model.addAttribute("teachers", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", 0);
            return "AdminLecturersList";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getLecturerAvatar(@PathVariable String id) {
        MajorLecturers lecturer = lecturesService.getLecturerById(id);
        if (lecturer != null && lecturer.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(lecturer.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}