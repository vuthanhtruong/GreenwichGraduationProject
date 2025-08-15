package com.example.demo.controller.AddByMajorStaff;

import com.example.demo.entity.AccountBalances;
import com.example.demo.entity.Authenticators;
import com.example.demo.entity.Students;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/students-list/")
public class AddStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final PersonsService personsService;
    private final AccountBalancesService accountBalancesService;
    private final AuthenticatorsService authenticatorsService;

    public AddStudentController(StaffsService staffsService, StudentsService studentsService,
                                PersonsService personsService, AccountBalancesService accountBalancesService,
                                AuthenticatorsService authenticatorsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.personsService = personsService;
        this.accountBalancesService = accountBalancesService;
        this.authenticatorsService = authenticatorsService;
    }

    @GetMapping("/add-student")
    public String showAddStudentPage(Model model) {
        model.addAttribute("student", new Students());
        return "AddStudent";
    }

    @PostMapping("/add-student")
    public String addStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();

        // Handle annotation-based validation
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        // Perform custom validations using StudentsService
        errors.addAll(studentsService.StudentValidation(student, avatarFile));

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatar", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    errors.add("Failed to store avatar temporarily: " + e.getMessage());
                }
            }
            return "AddStudent";
        }

        try {
            // Generate random password
            String randomPassword = studentsService.generateRandomPassword(12);

            // Generate unique student ID
            String studentId = studentsService.generateUniqueStudentId(
                    staffsService.getStaffMajor().getMajorId(),
                    student.getCreatedDate() != null ? student.getCreatedDate() : LocalDate.now());
            student.setId(studentId);

            // Handle avatar upload
            if (avatarFile != null && !avatarFile.isEmpty()) {
                byte[] avatarBytes = avatarFile.getBytes();
                student.setAvatar(avatarBytes);
            } else if (session.getAttribute("tempAvatar") != null) {
                student.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            // Add student using service
            studentsService.addStudents(student, randomPassword);

            // Create and save Authenticators entity
            Authenticators authenticators = new Authenticators();
            authenticators.setPersonId(studentId);
            authenticators.setPerson(personsService.getPersonById(studentId));
            authenticators.setPassword(randomPassword);
            authenticatorsService.createAuthenticator(authenticators);

            // Create and save AccountBalances entity
            AccountBalances accountBalances = new AccountBalances();
            accountBalances.setBalance(0.0);
            accountBalances.setStudent(studentsService.getStudentById(studentId));
            accountBalances.setStudentId(studentId);
            accountBalances.setLastUpdated(LocalDateTime.now());
            accountBalancesService.createAccountBalances(accountBalances);

            // Clear session data
            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
            return "redirect:/staff-home/students-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            return "AddStudent";
        } catch (Exception e) {
            errors.add("An error occurred while adding the student: " + e.getMessage());
            model.addAttribute("errors", errors);
            return "AddStudent";
        }
    }
}