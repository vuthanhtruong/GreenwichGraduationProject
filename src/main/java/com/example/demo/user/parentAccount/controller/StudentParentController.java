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

    // ===================== ADD PARENT =====================
    @PostMapping("/add-parent-to-student")
    public String addParentToStudent(
            @RequestParam @NotBlank(message = "Student ID is required") String studentId,
            @RequestParam @NotBlank(message = "Parent email is required") @Email(message = "Invalid email format") String parentEmail,
            @RequestParam(required = false) String supportPhoneNumber,
            @RequestParam(defaultValue = "MOTHER") String relationship,
            RedirectAttributes ra,
            HttpSession session) {

        // Check if student exists
        Students student = studentsService.getStudentById(studentId);
        if (student == null) {
            ra.addFlashAttribute("parentError", "Student not found.");
            return "redirect:/staff-home/students-list";
        }

        // Validate relationship enum
        RelationshipToStudent relEnum;
        try {
            relEnum = RelationshipToStudent.valueOf(relationship.toUpperCase());
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("parentError", "Invalid relationship to student.");
            session.setAttribute("studentId", studentId);
            return "redirect:/staff-home/students-list/edit-student-form";
        }

        String email = parentEmail.trim().toLowerCase();

        // NEW: Check if this email can be used for a parent account
        if (!parentAccountsService.isParentEmailAvailable(email)) {
            ra.addFlashAttribute("parentError",
                    "The email '" + parentEmail + "' is already used by another account type (student, teacher, admin, etc.). Please use a different email.");
            session.setAttribute("studentId", studentId);
            return "redirect:/staff-home/students-list/edit-student-form";
        }

        try {
            parentAccountsService.createParentLink(
                    studentId,
                    email,
                    supportPhoneNumber != null ? supportPhoneNumber.trim() : null,
                    relEnum.name()
            );

            ra.addFlashAttribute("parentSuccess",
                    "Parent added successfully! Login credentials have been sent to " + parentEmail);

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("parentError", "Failed to add parent: " + e.getMessage());
        }

        // Keep session to return cleanly (no ID in URL)
        session.setAttribute("studentId", studentId);
        return "redirect:/staff-home/students-list/edit-student-form";
    }

    // ===================== REMOVE PARENT LINK =====================
    @PostMapping("/remove-parent-link")
    public String removeParentLink(
            @RequestParam("studentId") String studentId,
            @RequestParam("parentId") String parentId,
            RedirectAttributes ra,
            HttpSession session) {

        try {
            parentAccountsService.removeParentLinkByIds(studentId, parentId);
            ra.addFlashAttribute("parentSuccess", "Parent link removed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("parentError", "Failed to remove parent link: " + e.getMessage());
        }

        session.setAttribute("studentId", studentId);
        return "redirect:/staff-home/students-list/edit-student-form";
    }
}