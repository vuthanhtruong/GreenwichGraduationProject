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
        model.addAttribute("father", new ParentAccounts());
        model.addAttribute("mother", new ParentAccounts());
        return "AddStudent";
    }

    @PostMapping("/add-student")
    public String addStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult studentBindingResult,
            @ModelAttribute("father") ParentAccounts father,
            @ModelAttribute("mother") ParentAccounts mother,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        List<String> errors = new ArrayList<>();

        // Handle student annotation-based validation
        if (studentBindingResult.hasErrors()) {
            studentBindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        // Perform custom validations for student
        errors.addAll(studentsService.StudentValidation(student, avatarFile));

        // Validate father and mother (if provided)
        if (isParentProvided(father)) {
            List<String> fatherErrors = parentAccountsService.validateParent(father);
            fatherErrors.forEach(error -> errors.add("Father: " + error));
        }
        if (isParentProvided(mother)) {
            List<String> motherErrors = parentAccountsService.validateParent(mother);
            motherErrors.forEach(error -> errors.add("Mother: " + error));
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
            // Generate random password for student
            String studentPassword = studentsService.generateRandomPassword(12);

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

            // Add student
            studentsService.addStudents(student, studentPassword);

            // Create and save Authenticators for student
            Authenticators studentAuth = new Authenticators();
            studentAuth.setPersonId(studentId);
            studentAuth.setPerson(personsService.getPersonById(studentId));
            studentAuth.setPassword(studentPassword);
            authenticatorsService.createAuthenticator(studentAuth);

            // Create and save AccountBalances for student
            AccountBalances accountBalances = new AccountBalances();
            accountBalances.setBalance(0.0);
            accountBalances.setStudent(studentsService.getStudentById(studentId));
            accountBalances.setStudentId(studentId);
            accountBalances.setLastUpdated(LocalDateTime.now());
            accountBalancesService.createAccountBalances(accountBalances);

            // Handle parents
            if (isParentProvided(father)) {
                father.setRelationshipToStudent("Father");
                ParentAccounts existingFather = parentAccountsService.findByEmail(father.getEmail());
                if (existingFather != null) {
                    // Update existing father's information
                    existingFather.setFirstName(father.getFirstName());
                    existingFather.setLastName(father.getLastName());
                    existingFather.setPhoneNumber(father.getPhoneNumber());
                    existingFather.setGender(father.getGender());
                    existingFather.setRelationshipToStudent("Father");
                    parentAccountsService.addParentAccounts(existingFather);
                    parentAccountsService.linkStudentToParent(student, existingFather);
                } else {
                    father.setId(parentAccountsService.generateUniqueParentId());
                    String fatherPassword = parentAccountsService.generateRandomPassword(12);
                    father.setPassword(fatherPassword);
                    parentAccountsService.addParentAccounts(father);

                    Authenticators fatherAuth = new Authenticators();
                    fatherAuth.setPersonId(father.getId());
                    fatherAuth.setPerson(father);
                    fatherAuth.setPassword(fatherPassword);
                    authenticatorsService.createAuthenticator(fatherAuth);

                    parentAccountsService.linkStudentToParent(student, father);
                }
            }

            if (isParentProvided(mother)) {
                mother.setRelationshipToStudent("Mother");
                ParentAccounts existingMother = parentAccountsService.findByEmail(mother.getEmail());
                if (existingMother != null) {
                    // Update existing mother's information
                    existingMother.setFirstName(mother.getFirstName());
                    existingMother.setLastName(mother.getLastName());
                    existingMother.setPhoneNumber(mother.getPhoneNumber());
                    existingMother.setGender(mother.getGender());
                    existingMother.setRelationshipToStudent("Mother");
                    parentAccountsService.addParentAccounts(existingMother);
                    parentAccountsService.linkStudentToParent(student, existingMother);
                } else {
                    mother.setId(parentAccountsService.generateUniqueParentId());
                    String motherPassword = parentAccountsService.generateRandomPassword(12);
                    mother.setPassword(motherPassword);
                    parentAccountsService.addParentAccounts(mother);

                    Authenticators motherAuth = new Authenticators();
                    motherAuth.setPersonId(mother.getId());
                    motherAuth.setPerson(mother);
                    motherAuth.setPassword(motherPassword);
                    authenticatorsService.createAuthenticator(motherAuth);

                    parentAccountsService.linkStudentToParent(student, mother);
                }
            }

            // Clear session data
            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("successMessage", "Student and parent accounts added successfully!");
            return "redirect:/staff-home/students-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            return "AddStudent";
        } catch (Exception e) {
            errors.add("An error occurred while adding the student or parents: " + e.getMessage());
            model.addAttribute("errors", errors);
            return "AddStudent";
        }
    }

    private boolean isParentProvided(ParentAccounts parent) {
        return parent != null && (
                parent.getFirstName() != null ||
                        parent.getLastName() != null ||
                        parent.getEmail() != null ||
                        parent.getPhoneNumber() != null);
    }
}