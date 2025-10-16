package com.example.demo.student.controller;

import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/student-home/personal-page")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPersonalPageController {
    private static final Logger logger = LoggerFactory.getLogger(StudentPersonalPageController.class);
    private final StudentsService studentsService;

    public StudentPersonalPageController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping
    public String showStudentPersonalPage(Model model, HttpSession session) {
        try {
            Students student = studentsService.getStudent();
            if (student == null) {
                logger.warn("No authenticated student found");
                model.addAttribute("errors", List.of("No authenticated student found."));
                model.addAttribute("student", new Students());
                model.addAttribute("avatarUrl", "/DefaultAvatar/Student_Male.png");
                return "StudentPersonalPage";
            }

            model.addAttribute("student", student);
            model.addAttribute("avatarUrl", student.getAvatar() != null ? "/student-home/personal-page/avatar/" + student.getId() : student.getDefaultAvatarPath());
            return "StudentPersonalPage";
        } catch (Exception e) {
            logger.error("Error loading personal page: {}", e.getMessage(), e);
            model.addAttribute("errors", List.of("Error loading personal page: " + e.getMessage()));
            model.addAttribute("student", new Students());
            model.addAttribute("avatarUrl", "/DefaultAvatar/Student_Male.png");
            return "StudentPersonalPage";
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