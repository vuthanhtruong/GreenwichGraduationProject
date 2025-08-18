package com.example.demo.controller.AddByMajorStaff;

import com.example.demo.entity.AccountBalances;
import com.example.demo.entity.Authenticators;
import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
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
    private final ParentAccountsService parentAccountsService;

    public AddStudentController(StaffsService staffsService, StudentsService studentsService,
                                PersonsService personsService, AccountBalancesService accountBalancesService,
                                AuthenticatorsService authenticatorsService, ParentAccountsService parentAccountsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.personsService = personsService;
        this.accountBalancesService = accountBalancesService;
        this.authenticatorsService = authenticatorsService;
        this.parentAccountsService = parentAccountsService;
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
            @RequestParam(value = "parentFirstName", required = false) String parentFirstName,
            @RequestParam(value = "parentLastName", required = false) String parentLastName,
            @RequestParam(value = "parentEmail", required = false) String parentEmail,
            @RequestParam(value = "parentPhoneNumber", required = false) String parentPhoneNumber,
            @RequestParam(value = "parentRelationship", required = false) String parentRelationship,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();

        // Validate student
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add("Student: " + error.getDefaultMessage()));
        }
        errors.addAll(studentsService.StudentValidation(student, avatarFile));

        // Validate parent if any field is provided
        ParentAccounts parent = null;
        boolean isNewParent = false;
        boolean isParentInfoProvided = (parentFirstName != null && !parentFirstName.trim().isEmpty()) ||
                (parentLastName != null && !parentLastName.trim().isEmpty()) ||
                (parentEmail != null && !parentEmail.trim().isEmpty()) ||
                (parentPhoneNumber != null && !parentPhoneNumber.trim().isEmpty()) ||
                (parentRelationship != null && !parentRelationship.trim().isEmpty());

        if (isParentInfoProvided) {
            parent = new ParentAccounts();
            parent.setFirstName(parentFirstName);
            parent.setLastName(parentLastName);
            parent.setEmail(parentEmail);
            parent.setPhoneNumber(parentPhoneNumber);
            // Validate parent fields only if they are provided
            List<String> parentErrors = parentAccountsService.ParentValidation(parent);
            parentErrors.forEach(error -> errors.add("Parent: " + error));

            // Validate relationship if provided
            if (parentRelationship != null && !parentRelationship.trim().isEmpty() && !isValidRelationship(parentRelationship)) {
                errors.add("Parent: Invalid relationship to student. Allowed values: Father, Mother, Guardian.");
            }
        }

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
            // Generate and set student ID
            String studentId = studentsService.generateUniqueStudentId(
                    staffsService.getStaffMajor().getMajorId(),
                    student.getCreatedDate() != null ? student.getCreatedDate() : LocalDate.now());
            student.setId(studentId);

            // Handle avatar
            if (avatarFile != null && !avatarFile.isEmpty()) {
                student.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatar") != null) {
                student.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            // Add student
            String studentPassword = studentsService.generateRandomPassword(12);
            studentsService.addStudents(student, studentPassword);

            // Create Authenticators for student
            Authenticators studentAuth = new Authenticators();
            studentAuth.setPersonId(studentId);
            studentAuth.setPerson(personsService.getPersonById(studentId));
            studentAuth.setPassword(studentPassword);
            authenticatorsService.createAuthenticator(studentAuth);

            // Create AccountBalances
            AccountBalances accountBalances = new AccountBalances();
            accountBalances.setBalance(0.0);
            accountBalances.setStudent(studentsService.getStudentById(studentId));
            accountBalances.setStudentId(studentId);
            accountBalances.setLastUpdated(LocalDateTime.now());
            accountBalancesService.createAccountBalances(accountBalances);

            // Handle parent account only if information is provided
            if (isParentInfoProvided && parent != null) {
                ParentAccounts existingParent = null;
                if (parent.getEmail() != null && !parent.getEmail().trim().isEmpty()) {
                    existingParent = parentAccountsService.findByEmail(parent.getEmail());
                }
                if (existingParent != null) {
                    parent = existingParent;
                } else {
                    String parentId = parentAccountsService.generateUniqueParentId();
                    parent.setId(parentId);
                    parent.setCreatedDate(LocalDate.now());
                    parentAccountsService.addParentAccounts(parent);
                    String parentPassword = parentAccountsService.generateRandomPassword(12);
                    Authenticators parentAuth = new Authenticators();
                    parentAuth.setPersonId(parentId);
                    parentAuth.setPerson(personsService.getPersonById(parentId));
                    parentAuth.setPassword(parentPassword);
                    authenticatorsService.createAuthenticator(parentAuth);
                    isNewParent = true;
                }
                // Only link student to parent if parent information is valid
                parentAccountsService.linkStudentToParent(studentsService.getStudentById(studentId), parent, parentRelationship);
            }

            // Clear session
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

    private boolean isValidRelationship(String relationship) {
        return relationship != null && (
                relationship.equalsIgnoreCase("Father") ||
                        relationship.equalsIgnoreCase("Mother") ||
                        relationship.equalsIgnoreCase("Guardian")
        );
    }
}