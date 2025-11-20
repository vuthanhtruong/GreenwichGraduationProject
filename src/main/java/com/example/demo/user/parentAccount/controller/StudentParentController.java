package com.example.demo.user.parentAccount.controller;

import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff-home/students-list")
public class StudentParentController {

    private final StudentsService studentsService;
    private final ParentAccountsService parentAccountsService;

    public StudentParentController(StudentsService studentsService,
                                   ParentAccountsService parentAccountsService) {
        this.studentsService = studentsService;
        this.parentAccountsService = parentAccountsService;
    }

    @PostMapping("/add-parent-to-student")
    public String addParentToStudent(
            @RequestParam @NotBlank(message = "Student ID is required") String studentId,
            @RequestParam @NotBlank(message = "Parent email is required")
            @Email(message = "Invalid parent email format") String parentEmail,
            @RequestParam(required = false) String supportPhoneNumber,
            @RequestParam(defaultValue = "MOTHER") String relationship,
            RedirectAttributes redirectAttributes, HttpSession session) {

        Students student = studentsService.getStudentById(studentId);
        if (student == null) {
            redirectAttributes.addFlashAttribute("parentError", "Student not found.");
            return "redirect:/staff-home/students-list";
        }

        RelationshipToStudent relEnum;
        try {
            relEnum = RelationshipToStudent.valueOf(relationship.toUpperCase());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("parentError", "Invalid relationship.");
            redirectAttributes.addAttribute("id", studentId);
            return "redirect:/staff-home/students-list/edit-student-form";
        }

        try {
            parentAccountsService.createParentLink(
                    studentId,
                    parentEmail.trim().toLowerCase(),
                    supportPhoneNumber != null ? supportPhoneNumber.trim() : null,
                    relEnum.name()
            );

            redirectAttributes.addFlashAttribute("parentSuccess",
                    "Parent added successfully! Login details have been sent to " + parentEmail);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("parentError",
                    "Failed to add parent: " + e.getMessage());
        }
        session.setAttribute("studentId", studentId);
        return "redirect:/staff-home/students-list/edit-student-form";
    }

    @PostMapping("/remove-parent-link")
    public String removeParentLink(
            @RequestParam("studentId") String studentId,
            @RequestParam("parentId") String parentId,
            RedirectAttributes ra,
            HttpSession session) {

        try {
            parentAccountsService.removeParentLinkByIds(studentId, parentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.setAttribute("studentId", studentId);

        return "redirect:/staff-home/students-list/edit-student-form";
    }
}