package com.example.demo.controller.AddByMajorStaff;

import com.example.demo.entity.AccountBalances;
import com.example.demo.entity.Authenticators;
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
@RequestMapping("/staff-home/students-list")
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
        errors.addAll(studentsService.StudentValidation(student, avatarFile));

        // Validate parent inputs only if any field is provided
        boolean isParent1Provided = isAnyFieldProvided(parentEmail1, supportPhoneNumber1, parentRelationship1);
        if (isParent1Provided) {
            errors.addAll(parentAccountsService.validateParentLink(parentEmail1, supportPhoneNumber1, parentRelationship1, "Parent 1"));
        }
        boolean isParent2Provided = isAnyFieldProvided(parentEmail2, supportPhoneNumber2, parentRelationship2);
        if (isParent2Provided) {
            errors.addAll(parentAccountsService.validateParentLink(parentEmail2, supportPhoneNumber2, parentRelationship2, "Parent 2"));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
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
            if (isParent1Provided) {
                parentAccountsService.createParentLink(studentId, parentEmail1, supportPhoneNumber1, parentRelationship1);
            }

            // Handle Parent 2
            if (isParent2Provided) {
                parentAccountsService.createParentLink(studentId, parentEmail2, supportPhoneNumber2, parentRelationship2);
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
            model.addAttribute("parentEmail1", parentEmail1);
            model.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            model.addAttribute("parentRelationship1", parentRelationship1);
            model.addAttribute("parentEmail2", parentEmail2);
            model.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            model.addAttribute("parentRelationship2", parentRelationship2);
            return "AddStudent";
        }
    }

    private boolean isAnyFieldProvided(String email, String phoneNumber, String relationship) {
        return (email != null && !email.trim().isEmpty()) ||
                (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
                (relationship != null && !relationship.trim().isEmpty());
    }
}