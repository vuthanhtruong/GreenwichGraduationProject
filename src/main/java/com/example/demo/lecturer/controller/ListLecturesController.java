package com.example.demo.lecturer.controller;

import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.Staff.model.Staffs;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.Staff.service.StaffsService;
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
public class ListLecturesController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public ListLecturesController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService=studentsService;
        this.lecturesService = lecturesService;
    }

    @GetMapping("/lecturers-list")
    public String listTeachers(
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

            Long totalTeachers = lecturesService.numberOfLecturers();

            if (totalTeachers == 0) {
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                return "LecturersList";
            }

            int totalPages = (int) Math.ceil((double) totalTeachers / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            int firstResult = (page - 1) * pageSize;

            List<MajorLecturers> teachers = lecturesService.getPaginatedLecturers(firstResult, pageSize);

            model.addAttribute("teachers", teachers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            return "LecturersList";
        } catch (SecurityException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    @GetMapping("/lecturers-list/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getlectureAvatar(@PathVariable String id) {
        MajorLecturers lectures = lecturesService.getLecturerById(id);
        if (lectures != null && lectures.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust based on your avatar format (JPEG, PNG, etc.)
                    .body(lectures.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}
