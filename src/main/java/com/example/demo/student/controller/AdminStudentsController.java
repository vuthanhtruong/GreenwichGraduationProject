package com.example.demo.student.controller;

import com.example.demo.admin.service.AdminsService;
import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
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
@RequestMapping("/admin-home/students-list")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentsController {

    private final StudentsService studentsService;
    private final AdminsService adminsService;

    public AdminStudentsController(StudentsService studentsService, AdminsService adminsService) {
        this.studentsService = studentsService;
        this.adminsService = adminsService;
    }

    @GetMapping("")
    public String listStudents(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            String campusId = adminsService.getAdminCampus() != null ? adminsService.getAdminCampus().getCampusId() : null;
            if (campusId == null) {
                model.addAttribute("error", "Admin campus not found.");
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 5);
                model.addAttribute("totalStudents", 0);
                return "AdminStudentsList";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("studentPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            pageSize = Math.max(1, pageSize);
            session.setAttribute("studentPageSize", pageSize);

            Long totalStudents = studentsService.totalStudentsByCampus(campusId);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalStudents / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("studentPage", page);
            session.setAttribute("studentTotalPages", totalPages);

            if (totalStudents == 0) {
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalStudents", 0);
                model.addAttribute("message", "No students found.");
                model.addAttribute("campusId", campusId);
                return "AdminStudentsList";
            }

            int firstResult = (page - 1) * pageSize;
            List<Students> students = studentsService.getPaginatedStudentsByCampus(campusId, firstResult, pageSize);

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("campusId", campusId);
            return "AdminStudentsList";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            model.addAttribute("students", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStudents", 0);
            return "AdminStudentsList";
        }
    }

    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getStudentAvatar(@PathVariable String id) {
        Students student = studentsService.getStudentById(id);
        if (student != null && student.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(student.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}