package com.example.demo.lecturer.controller;

import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.staff.service.StaffsService;
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
@RequestMapping("/staff-home/lecturers-list")
public class ListLecturersController {

    private final LecturesService lecturesService;

    public ListLecturersController(StaffsService staffsService, LecturesService lecturesService) {

        this.lecturesService = lecturesService;
    }

    @GetMapping("")
    public String listLecturers(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("lecturerPageSize");
                if (pageSize == null) {
                    pageSize = 5;
                }
            }
            session.setAttribute("lecturerPageSize", pageSize);

            Long totalLecturers = lecturesService.numberOfLecturers();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalLecturers / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("lecturerPage", page);
            session.setAttribute("lecturerTotalPages", totalPages);

            if (totalLecturers == 0) {
                model.addAttribute("teachers", new ArrayList<>());
                model.addAttribute("lecturer", new MajorLecturers());
                model.addAttribute("editLecturer", new MajorLecturers());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalLecturers", 0);
                model.addAttribute("alertClass", "alert-warning");
                return "LecturersList";
            }

            int firstResult = (page - 1) * pageSize;
            List<MajorLecturers> teachers = lecturesService.getPaginatedLecturers(firstResult, pageSize);

            model.addAttribute("teachers", teachers);
            model.addAttribute("lecturer", new MajorLecturers());
            model.addAttribute("editLecturer", new MajorLecturers());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", totalLecturers);
            return "LecturersList";
        } catch (SecurityException e) {
            model.addAttribute("errors", List.of("Security error: " + e.getMessage()));
            model.addAttribute("lecturer", new MajorLecturers());
            model.addAttribute("editLecturer", new MajorLecturers());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalLecturers", 0);
            return "LecturersList";
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