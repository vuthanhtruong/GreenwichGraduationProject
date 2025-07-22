package com.example.demo.controller;
import com.example.demo.entity.Students;
import com.example.demo.service.StaffsService;
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

    public DeleteStudentController(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @DeleteMapping("/delete-student/{id}")
    public String deleteStudent(@PathVariable String id, RedirectAttributes redirectAttributes) {
        staffsService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("message", "Delete student ID member: " + id);
        return "redirect:/staff-home/students-list";
    }

}
