package com.example.demo.student.controller;

import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.student.model.Students;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.authenticator.service.AuthenticatorsService;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.parentAccount.service.ParentAccountsService;
import com.example.demo.person.service.PersonsService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/staff-home/students-list")
public class EditStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final ResourceLoader resourceLoader;
    private final PersonsService personsService;
    private final ParentAccountsService parentAccountsService;
    private final AuthenticatorsService authenticatorsService;

    public EditStudentController(StaffsService staffsService, LecturesService lecturesService,
                                 StudentsService studentsService, ResourceLoader resourceLoader,
                                 PersonsService personsService, ParentAccountsService parentAccountsService,
                                 AuthenticatorsService authenticatorsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.lecturesService = lecturesService;
        this.resourceLoader = resourceLoader;
        this.personsService = personsService;
        this.parentAccountsService = parentAccountsService;
        this.authenticatorsService = authenticatorsService;
    }

    @PostMapping("/edit-student-form")
    public String handleEditStudentPost(
            @RequestParam String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            Model model, RedirectAttributes redirectAttributes) {
        Students student = studentsService.getStudentById(id);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Student with ID " + id + " not found.");
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/search-students";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/students-list";
        }
        List<Student_ParentAccounts> parentLinks = parentAccountsService.getParentLinksByStudentId(id);
        model.addAttribute("student", student);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
        model.addAttribute("parentLinks", parentLinks);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        model.addAttribute("source", source);
        return "EditStudentForm";
    }

    @PutMapping("/edit-student-form")
    public String editStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "parentEmail1", required = false) String parentEmail1,
            @RequestParam(value = "supportPhoneNumber1", required = false) String supportPhoneNumber1,
            @RequestParam(value = "parentRelationship1", required = false) String parentRelationship1,
            @RequestParam(value = "parentEmail2", required = false) String parentEmail2,
            @RequestParam(value = "supportPhoneNumber2", required = false) String supportPhoneNumber2,
            @RequestParam(value = "parentRelationship2", required = false) String parentRelationship2,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap,
            HttpSession httpSession) {

        List<String> errors = new ArrayList<>();
        errors.addAll(studentsService.StudentValidation(student, avatarFile));
        if (student.getEmail().equals(parentEmail1) || student.getEmail().equals(parentEmail2)) {
            errors.add("Student and parent emails cannot be duplicated.");
        }
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
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
            modelMap.addAttribute("parentLinks", parentAccountsService.getParentLinksByStudentId(student.getId()));
            modelMap.addAttribute("parentEmail1", parentEmail1);
            modelMap.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            modelMap.addAttribute("parentRelationship1", parentRelationship1);
            modelMap.addAttribute("parentEmail2", parentEmail2);
            modelMap.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            modelMap.addAttribute("parentRelationship2", parentRelationship2);
            modelMap.addAttribute("searchType", searchType);
            modelMap.addAttribute("keyword", keyword);
            modelMap.addAttribute("page", page);
            modelMap.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            modelMap.addAttribute("source", source);
            httpSession.setAttribute("avatarStudent", "/staff-home/students-list/avatar/" + student.getId());
            return "EditStudentForm";
        }

        try {
            // Check if student exists
            if (!personsService.existsPersonById(student.getId())) {
                redirectAttributes.addFlashAttribute("error", "Student with ID " + student.getId() + " not found.");
                if (source.equals("search")) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    httpSession.removeAttribute("avatarStudent");
                    return "redirect:/staff-home/search-students";
                }
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                httpSession.removeAttribute("avatarStudent");
                return "redirect:/staff-home/students-list";
            }

            // Handle avatar upload
            if (avatarFile != null && !avatarFile.isEmpty()) {
                student.setAvatar(avatarFile.getBytes());
            } else {
                Students existingStudent = studentsService.getStudentById(student.getId());
                student.setAvatar(existingStudent.getAvatar());
            }

            // Edit student
            studentsService.editStudent(student.getId(), student);

            // Handle parent accounts
            List<Student_ParentAccounts> currentParentLinks = parentAccountsService.getParentLinksByStudentId(student.getId());
            Student_ParentAccounts parent1Link = currentParentLinks.size() > 0 ? currentParentLinks.get(0) : null;
            Student_ParentAccounts parent2Link = currentParentLinks.size() > 1 ? currentParentLinks.get(1) : null;

            // Process Parent 1
            if (isParent1Provided) {
                parentAccountsService.editOrCreateParentLink(student.getId(), parent1Link, parentEmail1, supportPhoneNumber1, parentRelationship1);
            } else if (parent1Link != null) {
                parentAccountsService.removeParentLink(parent1Link);
                parentAccountsService.deleteIfUnlinked(parent1Link.getParent(), student.getId());
            }

            // Process Parent 2
            if (isParent2Provided) {
                parentAccountsService.editOrCreateParentLink(student.getId(), parent2Link, parentEmail2, supportPhoneNumber2, parentRelationship2);
            } else if (parent2Link != null) {
                parentAccountsService.removeParentLink(parent2Link);
                parentAccountsService.deleteIfUnlinked(parent2Link.getParent(), student.getId());
            }

            redirectAttributes.addFlashAttribute("successMessage", "Student edited successfully!");
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                httpSession.removeAttribute("avatarStudent");
                return "redirect:/staff-home/search-students";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            httpSession.removeAttribute("avatarStudent");
            return "redirect:/staff-home/students-list";
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
            modelMap.addAttribute("parentLinks", parentAccountsService.getParentLinksByStudentId(student.getId()));
            modelMap.addAttribute("parentEmail1", parentEmail1);
            modelMap.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            modelMap.addAttribute("parentRelationship1", parentRelationship1);
            modelMap.addAttribute("parentEmail2", parentEmail2);
            modelMap.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            modelMap.addAttribute("parentRelationship2", parentRelationship2);
            modelMap.addAttribute("searchType", searchType);
            modelMap.addAttribute("keyword", keyword);
            modelMap.addAttribute("page", page);
            modelMap.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            modelMap.addAttribute("source", source);
            httpSession.setAttribute("avatarStudent", "/staff-home/students-list/avatar/" + student.getId());
            return "EditStudentForm";
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Database error while updating student: " + e.getMessage());
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                httpSession.removeAttribute("avatarStudent");
                return "redirect:/staff-home/search-students";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            httpSession.removeAttribute("avatarStudent");
            return "redirect:/staff-home/students-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unexpected error while updating student: " + e.getMessage());
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                httpSession.removeAttribute("avatarStudent");
                return "redirect:/staff-home/search-students";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            httpSession.removeAttribute("avatarStudent");
            return "redirect:/staff-home/students-list";
        }
    }

    private boolean isAnyFieldProvided(String email, String phoneNumber, String relationship) {
        return (email != null && !email.trim().isEmpty()) ||
                (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
                (relationship != null && !relationship.trim().isEmpty());
    }
}