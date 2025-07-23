package com.example.demo.controller;
import com.example.demo.entity.Students;
import com.example.demo.service.LecturesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home/students-list")
public class DeleteStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;

    public DeleteStudentController(StaffsService staffsService, LecturesService lecturesService, StudentsService studentsService) {
        this.staffsService = staffsService;
        this.studentsService=studentsService;
        this.lecturesService = lecturesService;
    }

    @DeleteMapping("/delete-student/{id}")
    public String deleteStudent(@PathVariable String id, RedirectAttributes redirectAttributes) {
        studentsService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("message", "Delete student ID member: " + id);
        return "redirect:/staff-home/students-list";
    }
}
