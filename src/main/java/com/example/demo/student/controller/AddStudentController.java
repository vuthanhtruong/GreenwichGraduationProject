package com.example.demo.student.controller;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.authenticator.model.Authenticators;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.parentAccount.service.ParentAccountsService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.student.model.Students;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final CurriculumService  curriculumService;

    public AddStudentController(StaffsService staffsService, StudentsService studentsService,
                                PersonsService personsService, AccountBalancesService accountBalancesService,
                                AuthenticatorsService authenticatorsService, ParentAccountsService parentAccountsService,
                                EmailServiceForStudentService emailServiceForStudentService, CurriculumService curriculumService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.personsService = personsService;
        this.accountBalancesService = accountBalancesService;
        this.authenticatorsService = authenticatorsService;
        this.parentAccountsService = parentAccountsService;
        this.emailServiceForStudentService = emailServiceForStudentService;
        this.curriculumService = curriculumService;
    }

    @PostMapping("/add-student")
    public String addStudent(
            @ModelAttribute("student") Students student,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "curriculum", required = true) String curriculumId,
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
        if (student.getEmail() != null && (student.getEmail().equals(parentEmail1) || student.getEmail().equals(parentEmail2))) {
            errors.add("Student and parent emails cannot be duplicated.");
        }
        boolean isParent1Provided = isAnyFieldProvided(parentEmail1, supportPhoneNumber1, parentRelationship1);
        if (isParent1Provided) {
            errors.addAll(parentAccountsService.validateParentLink(parentEmail1, supportPhoneNumber1, parentRelationship1, "Parent 1"));
        }
        boolean isParent2Provided = isAnyFieldProvided(parentEmail2, supportPhoneNumber2, parentRelationship2);
        if (isParent2Provided) {
            errors.addAll(parentAccountsService.validateParentLink(parentEmail2, supportPhoneNumber2, parentRelationship2, "Parent 2"));
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
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    session.setAttribute("tempAvatar", avatarFile.getBytes());
                    session.setAttribute("tempAvatarName", avatarFile.getOriginalFilename());
                } catch (IOException e) {
                    errors.add("Failed to store avatar temporarily: " + e.getMessage());
                }
            }
            return "StudentsList";
        }

        try {
            String studentId = studentsService.generateUniqueStudentId(
                    staffsService.getStaff().getMajorManagement().getMajorId(),
                    student.getCreatedDate() != null ? student.getCreatedDate() : LocalDate.now());
            student.setId(studentId);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                student.setAvatar(avatarFile.getBytes());
            } else if (session.getAttribute("tempAvatar") != null) {
                student.setAvatar((byte[]) session.getAttribute("tempAvatar"));
            }
            Curriculum curriculum=curriculumService.getCurriculumById(curriculumId);

            String studentPassword = studentsService.generateRandomPassword(12);
            studentsService.addStudents(student, curriculum,studentPassword);

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
            errors.add("Failed to process avatar: " + e.getMessage());
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
            return "StudentsList";
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
            model.addAttribute("students", studentsService.getPaginatedStudents(0, (Integer) session.getAttribute("pageSize")));
            model.addAttribute("currentPage", session.getAttribute("currentPage") != null ? session.getAttribute("currentPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("totalPages") != null ? session.getAttribute("totalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("pageSize") != null ? session.getAttribute("pageSize") : 5);
            model.addAttribute("totalStudents", studentsService.numberOfStudents());
            return "StudentsList";
        }
    }

    private boolean isAnyFieldProvided(String email, String phoneNumber, String relationship) {
        return (email != null && !email.trim().isEmpty()) ||
                (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
                (relationship != null && !relationship.trim().isEmpty());
    }
}