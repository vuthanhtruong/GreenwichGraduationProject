package com.example.demo.student.controller;

import com.example.demo.majorStaff.model.Staffs;
import com.example.demo.student.model.Students;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.majorStaff.service.StaffsService;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
public class ListStudentsController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public ListStudentsController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
    }

    @GetMapping("/students-list")
    public String listStudents(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            Staffs staffs = staffsService.getStaff();

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("pageSize", pageSize);

            Long totalStudents = studentsService.numberOfStudents();

            if (totalStudents == 0) {
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                return "StudentsList";
            }

            int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            int firstResult = (page - 1) * pageSize;

            List<Students> students = studentsService.getPaginatedStudents(firstResult, pageSize);

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            return "StudentsList";
        } catch (SecurityException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/students-list/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getStudentAvatar(@PathVariable String id) {
        Students student = studentsService.getStudentById(id);
        if (student != null && student.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust based on your avatar format (JPEG, PNG, etc.)
                    .body(student.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}