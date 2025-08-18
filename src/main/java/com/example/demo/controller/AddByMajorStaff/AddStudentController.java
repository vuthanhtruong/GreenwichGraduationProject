package com.example.demo.controller.AddByMajorStaff;

import com.example.demo.entity.AccountBalances;
import com.example.demo.entity.Authenticators;
import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;
import com.example.demo.entity.Enums.RelationshipToStudent;
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
    private final EmailServiceForStudentService emailServiceForStudentService;

    public AddStudentController(StaffsService staffsService, StudentsService studentsService,
                                PersonsService personsService, AccountBalancesService accountBalancesService,
                                AuthenticatorsService authenticatorsService, ParentAccountsService parentAccountsService,
                                EmailServiceForStudentService emailServiceForStudentService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.personsService = personsService;
        this.accountBalancesService = accountBalancesService;
        this.authenticatorsService = authenticatorsService;
        this.parentAccountsService = parentAccountsService;
        this.emailServiceForStudentService = emailServiceForStudentService;
    }

    @GetMapping("/add-student")
    public String showAddStudentPage(Model model) {
        model.addAttribute("student", new Students());
        model.addAttribute("relationshipTypes", RelationshipToStudent.values());
        return "AddStudent";
    }

    @PostMapping("/add-student")
    public String addStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "parentEmail1", required = false) String parentEmail1,
            @RequestParam(value = "supportPhoneNumber1", required = false) String supportPhoneNumber1,
            @RequestParam(value = "parentRelationship1", required = false) String parentRelationship1,
            @RequestParam(value = "parentEmail2", required = false) String parentEmail2,
            @RequestParam(value = "supportPhoneNumber2", required = false) String supportPhoneNumber2,
            @RequestParam(value = "parentRelationship2", required = false) String parentRelationship2,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();

        // Validate student
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add("Student: " + error.getDefaultMessage()));
        }
        errors.addAll(studentsService.StudentValidation(student, avatarFile));

        // Validate Parent 1
        boolean isParent1InfoProvided = (parentEmail1 != null && !parentEmail1.trim().isEmpty()) ||
                (supportPhoneNumber1 != null && !supportPhoneNumber1.trim().isEmpty());
        ParentAccounts parent1 = null;
        boolean isNewParent1 = false;
        if (isParent1InfoProvided) {
            parent1 = new ParentAccounts();
            parent1.setEmail(parentEmail1);
            List<String> parentErrors = parentAccountsService.ParentValidation(parent1);
            parentErrors.forEach(error -> errors.add("Parent 1: " + error));

            if (supportPhoneNumber1 != null && !supportPhoneNumber1.trim().isEmpty()) {
                if (!supportPhoneNumber1.matches("^\\+?[0-9]{10,15}$")) {
                    errors.add("Parent 1: Invalid support phone number format. Must be 10-15 digits, optionally starting with '+'.");
                }
            }

            if (parentRelationship1 != null && !parentRelationship1.trim().isEmpty()) {
                try {
                    RelationshipToStudent.valueOf(parentRelationship1.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Parent 1: Invalid relationship to student. Allowed values: " +
                            String.join(", ", getRelationshipValues()));
                }
            }

            if (parent1.getEmail() == null || parent1.getEmail().trim().isEmpty()) {
                isParent1InfoProvided = false;
            }
        }

        // Validate Parent 2
        boolean isParent2InfoProvided = (parentEmail2 != null && !parentEmail2.trim().isEmpty()) ||
                (supportPhoneNumber2 != null && !supportPhoneNumber2.trim().isEmpty());
        ParentAccounts parent2 = null;
        boolean isNewParent2 = false;
        if (isParent2InfoProvided) {
            parent2 = new ParentAccounts();
            parent2.setEmail(parentEmail2);
            List<String> parentErrors = parentAccountsService.ParentValidation(parent2);
            parentErrors.forEach(error -> errors.add("Parent 2: " + error));

            if (supportPhoneNumber2 != null && !supportPhoneNumber2.trim().isEmpty()) {
                if (!supportPhoneNumber2.matches("^\\+?[0-9]{10,15}$")) {
                    errors.add("Parent 2: Invalid support phone number format. Must be 10-15 digits, optionally starting with '+'.");
                }
            }

            if (parentRelationship2 != null && !parentRelationship2.trim().isEmpty()) {
                try {
                    RelationshipToStudent.valueOf(parentRelationship2.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Parent 2: Invalid relationship to student. Allowed values: " +
                            String.join(", ", getRelationshipValues()));
                }
            }

            if (parent2.getEmail() == null || parent2.getEmail().trim().isEmpty()) {
                isParent2InfoProvided = false;
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            // Keep parent input values
            model.addAttribute("parentEmail1", parentEmail1);
            model.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            model.addAttribute("parentRelationship1", parentRelationship1);
            model.addAttribute("parentEmail2", parentEmail2);
            model.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            model.addAttribute("parentRelationship2", parentRelationship2);
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
                    staffsService.getStaff().getMajorManagement().getMajorId(),
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

            // Handle Parent 1
            if (isParent1InfoProvided && parent1 != null) {
                ParentAccounts existingParent = parentAccountsService.findByEmail(parent1.getEmail());
                if (existingParent != null) {
                    parent1 = existingParent;
                } else {
                    String parentId = parentAccountsService.generateUniqueParentId();
                    parent1.setId(parentId);
                    parent1.setCreatedDate(LocalDate.now());
                    parentAccountsService.addParentAccounts(parent1);
                    String parentPassword = parentAccountsService.generateRandomPassword(12);
                    Authenticators parentAuth = new Authenticators();
                    parentAuth.setPersonId(parentId);
                    parentAuth.setPerson(personsService.getPersonById(parentId));
                    parentAuth.setPassword(parentPassword);
                    authenticatorsService.createAuthenticator(parentAuth);
                    isNewParent1 = true;
                }
                RelationshipToStudent relationshipEnum1 = null;
                if (parentRelationship1 != null && !parentRelationship1.trim().isEmpty()) {
                    relationshipEnum1 = RelationshipToStudent.valueOf(parentRelationship1.toUpperCase());
                }
                Student_ParentAccounts studentParent1 = new Student_ParentAccounts(
                        studentsService.getStudentById(studentId),
                        parent1,
                        staffsService.getStaff(),
                        LocalDateTime.now(),
                        relationshipEnum1,
                        supportPhoneNumber1
                );
                parentAccountsService.linkStudentToParent(studentParent1);
            }

            // Handle Parent 2
            if (isParent2InfoProvided && parent2 != null) {
                ParentAccounts existingParent = parentAccountsService.findByEmail(parent2.getEmail());
                if (existingParent != null) {
                    parent2 = existingParent;
                } else {
                    String parentId = parentAccountsService.generateUniqueParentId();
                    parent2.setId(parentId);
                    parent2.setCreatedDate(LocalDate.now());
                    parentAccountsService.addParentAccounts(parent2);
                    String parentPassword = parentAccountsService.generateRandomPassword(12);
                    Authenticators parentAuth = new Authenticators();
                    parentAuth.setPersonId(parentId);
                    parentAuth.setPerson(personsService.getPersonById(parentId));
                    parentAuth.setPassword(parentPassword);
                    authenticatorsService.createAuthenticator(parentAuth);
                    isNewParent2 = true;
                }
                RelationshipToStudent relationshipEnum2 = null;
                if (parentRelationship2 != null && !parentRelationship2.trim().isEmpty()) {
                    relationshipEnum2 = RelationshipToStudent.valueOf(parentRelationship2.toUpperCase());
                }
                Student_ParentAccounts studentParent2 = new Student_ParentAccounts(
                        studentsService.getStudentById(studentId),
                        parent2,
                        staffsService.getStaff(),
                        LocalDateTime.now(),
                        relationshipEnum2,
                        supportPhoneNumber2
                );
                parentAccountsService.linkStudentToParent(studentParent2);
            }

            // Clear session
            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
            return "redirect:/staff-home/students-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            // Keep parent input values
            model.addAttribute("parentEmail1", parentEmail1);
            model.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            model.addAttribute("parentRelationship1", parentRelationship1);
            model.addAttribute("parentEmail2", parentEmail2);
            model.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            model.addAttribute("parentRelationship2", parentRelationship2);
            return "AddStudent";
        } catch (Exception e) {
            errors.add("An error occurred while adding the student: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            // Keep parent input values
            model.addAttribute("parentEmail1", parentEmail1);
            model.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            model.addAttribute("parentRelationship1", parentRelationship1);
            model.addAttribute("parentEmail2", parentEmail2);
            model.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            model.addAttribute("parentRelationship2", parentRelationship2);
            return "AddStudent";
        }
    }

    private String[] getRelationshipValues() {
        RelationshipToStudent[] values = RelationshipToStudent.values();
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i].toString();
        }
        return result;
    }
}