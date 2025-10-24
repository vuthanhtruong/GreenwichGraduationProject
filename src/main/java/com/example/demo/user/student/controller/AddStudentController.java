package com.example.demo.user.student.controller;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.specialization.service.SpecializationService;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.specialization.model.Specialization;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/students-list")
public class AddStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final PersonsService personsService;
    private final AccountBalancesService accountBalancesService;
    private final AuthenticatorsService authenticatorsService;
    private final ParentAccountsService parentAccountsService;
    private final CurriculumService curriculumService;
    private final SpecializationService specializationService;

    public AddStudentController(StaffsService staffsService, StudentsService studentsService,
                                PersonsService personsService, AccountBalancesService accountBalancesService,
                                AuthenticatorsService authenticatorsService, ParentAccountsService parentAccountsService,
                                CurriculumService curriculumService,
                                SpecializationService specializationService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.personsService = personsService;
        this.accountBalancesService = accountBalancesService;
        this.authenticatorsService = authenticatorsService;
        this.parentAccountsService = parentAccountsService;
        this.curriculumService = curriculumService;
        this.specializationService = specializationService;
    }

    @PostMapping("/add-student")
    public String addStudent(
            @ModelAttribute("student") Students student,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "curriculumId", required = true) String curriculumId,
            @RequestParam(value = "specializationId", required = true) String specializationId,
            @RequestParam(value = "parentEmail1", required = false) String parentEmail1,
            @RequestParam(value = "supportPhoneNumber1", required = false) String supportPhoneNumber1,
            @RequestParam(value = "parentRelationship1", required = false) String parentRelationship1,
            @RequestParam(value = "parentEmail2", required = false) String parentEmail2,
            @RequestParam(value = "supportPhoneNumber2", required = false) String supportPhoneNumber2,
            @RequestParam(value = "parentRelationship2", required = false) String parentRelationship2,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = new HashMap<>();
        errors.putAll(studentsService.StudentValidation(student, avatarFile));

        // Check for duplicate emails between student and parents
        if (student.getEmail() != null && (student.getEmail().equals(parentEmail1) || student.getEmail().equals(parentEmail2))) {
            errors.put("email", "Student and parent emails cannot be duplicated.");
        }

        // Validate parent inputs only if any field is provided
        boolean isParent1Provided = isAnyFieldProvided(parentEmail1, supportPhoneNumber1, parentRelationship1);
        if (isParent1Provided) {
            errors.putAll(parentAccountsService.validateParentLink(parentEmail1, supportPhoneNumber1, parentRelationship1, "Parent 1"));
        }
        boolean isParent2Provided = isAnyFieldProvided(parentEmail2, supportPhoneNumber2, parentRelationship2);
        if (isParent2Provided) {
            errors.putAll(parentAccountsService.validateParentLink(parentEmail2, supportPhoneNumber2, parentRelationship2, "Parent 2"));
        }

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            model.addAttribute("parentEmail1", parentEmail1);
            model.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            model.addAttribute("parentRelationship1", parentRelationship1);
            model.addAttribute("parentEmail2", parentEmail2);
            model.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            model.addAttribute("parentRelationship2", parentRelationship2);
            model.addAttribute("students", studentsService.getPaginatedStudents(0, (Integer) session.getAttribute("pageSize")));
            model.addAttribute("currentPage", session.getAttribute("currentPage") != null ? session.getAttribute("currentPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("totalPages") != null ? session.getAttribute("totalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("pageSize") != null ? session.getAttribute("pageSize") : 5);
            model.addAttribute("totalStudents", studentsService.numberOfStudents());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatar", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    errors.put("avatarFile", "Failed to store avatar temporarily: " + e.getMessage());
                }
            }
            return "StudentsList";
        }

        try {
            String studentId = studentsService.generateUniqueStudentId(
                    specializationId,
                    student.getCreatedDate() != null ? student.getCreatedDate() : LocalDate.now());
            student.setId(studentId);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                student.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatar") != null) {
                student.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }

            Specialization specialization = specializationService.getSpecializationById(specializationId);
            Curriculum curriculum = curriculumService.getCurriculumById(curriculumId);

            String studentPassword = studentsService.generateRandomPassword(12);
            studentsService.addStudents(student, curriculum, specialization, studentPassword);

            Authenticators studentAuth = new Authenticators();
            studentAuth.setPersonId(studentId);
            studentAuth.setPerson(personsService.getPersonById(studentId));
            studentAuth.setPassword(studentPassword);
            authenticatorsService.createAuthenticator(studentAuth);

            AccountBalances accountBalances = new AccountBalances();
            accountBalances.setBalance(0.0);
            accountBalances.setStudent(studentsService.getStudentById(studentId));
            accountBalances.setStudentId(studentId);
            accountBalances.setLastUpdated(LocalDateTime.now());
            accountBalancesService.createAccountBalances(accountBalances);

            if (isParent1Provided) {
                parentAccountsService.createParentLink(studentId, parentEmail1, supportPhoneNumber1, parentRelationship1);
            }
            if (isParent2Provided) {
                parentAccountsService.createParentLink(studentId, parentEmail2, supportPhoneNumber2, parentRelationship2);
            }

            session.removeAttribute("tempAvatar");
            session.removeAttribute("tempAvatarName");

            redirectAttributes.addFlashAttribute("message", "Student added successfully!");
            return "redirect:/staff-home/students-list";
        } catch (IOException e) {
            errors.put("general", "Failed to process avatar: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            model.addAttribute("parentEmail1", parentEmail1);
            model.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            model.addAttribute("parentRelationship1", parentRelationship1);
            model.addAttribute("parentEmail2", parentEmail2);
            model.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            model.addAttribute("parentRelationship2", parentRelationship2);
            model.addAttribute("students", studentsService.getPaginatedStudents(0, (Integer) session.getAttribute("pageSize")));
            model.addAttribute("currentPage", session.getAttribute("currentPage") != null ? session.getAttribute("currentPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("totalPages") != null ? session.getAttribute("totalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("pageSize") != null ? session.getAttribute("pageSize") : 5);
            model.addAttribute("totalStudents", studentsService.numberOfStudents());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "StudentsList";
        } catch (Exception e) {
            errors.put("general", "An error occurred while adding the student: " + e.getMessage());
            model.addAttribute("errors", errors);
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            model.addAttribute("parentEmail1", parentEmail1);
            model.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            model.addAttribute("parentRelationship1", parentRelationship1);
            model.addAttribute("parentEmail2", parentEmail2);
            model.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            model.addAttribute("parentRelationship2", parentRelationship2);
            model.addAttribute("students", studentsService.getPaginatedStudents(0, (Integer) session.getAttribute("pageSize")));
            model.addAttribute("currentPage", session.getAttribute("currentPage") != null ? session.getAttribute("currentPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("totalPages") != null ? session.getAttribute("totalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("pageSize") != null ? session.getAttribute("pageSize") : 5);
            model.addAttribute("totalStudents", studentsService.numberOfStudents());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "StudentsList";
        }
    }

    private boolean isAnyFieldProvided(String email, String phoneNumber, String relationship) {
        return (email != null && !email.trim().isEmpty()) ||
                (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
                (relationship != null && !relationship.trim().isEmpty());
    }
}