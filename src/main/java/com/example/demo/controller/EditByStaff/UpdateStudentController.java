package com.example.demo.controller.EditByStaff;

import com.example.demo.entity.Authenticators;
import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.service.AuthenticatorsService;
import com.example.demo.service.LecturesService;
import com.example.demo.service.ParentAccountsService;
import com.example.demo.service.PersonsService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.StudentsService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/staff-home/students-list")
public class UpdateStudentController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final LecturesService lecturesService;
    private final ResourceLoader resourceLoader;
    private final PersonsService personsService;
    private final ParentAccountsService parentAccountsService;
    private final AuthenticatorsService authenticatorsService;

    public UpdateStudentController(StaffsService staffsService, LecturesService lecturesService,
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
    public String handleEditStudentPost(@RequestParam String id, Model model) {
        Students student = studentsService.getStudentById(id);
        if (student == null) {
            return "redirect:/staff-home/students-list?error=Student+not+found";
        }
        // Load current parent links
        List<Student_ParentAccounts> parentLinks = parentAccountsService.getParentLinksByStudentId(id);
        model.addAttribute("student", student);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
        model.addAttribute("parentLinks", parentLinks);
        return "EditStudentForm";
    }

    @PutMapping("/edit-student-form")
    public String updateStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "parentEmail1", required = false) String parentEmail1,
            @RequestParam(value = "supportPhoneNumber1", required = false) String supportPhoneNumber1,
            @RequestParam(value = "parentRelationship1", required = false) String parentRelationship1,
            @RequestParam(value = "parentEmail2", required = false) String parentEmail2,
            @RequestParam(value = "supportPhoneNumber2", required = false) String supportPhoneNumber2,
            @RequestParam(value = "parentRelationship2", required = false) String parentRelationship2,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap,
            HttpSession httpSession) {

        List<String> errors = new ArrayList<>();
        validateStudent(student, bindingResult, avatarFile, errors);

        // Validate Parent 1
        boolean isParent1InfoProvided = (parentEmail1 != null && !parentEmail1.trim().isEmpty()) ||
                (supportPhoneNumber1 != null && !supportPhoneNumber1.trim().isEmpty());
        ParentAccounts parent1 = null;
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
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
            modelMap.addAttribute("parentLinks", parentAccountsService.getParentLinksByStudentId(student.getId()));
            // Keep parent input values
            modelMap.addAttribute("parentEmail1", parentEmail1);
            modelMap.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            modelMap.addAttribute("parentRelationship1", parentRelationship1);
            modelMap.addAttribute("parentEmail2", parentEmail2);
            modelMap.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            modelMap.addAttribute("parentRelationship2", parentRelationship2);
            httpSession.setAttribute("avatarStudent", "/staff-home/students-list/avatar/" + student.getId());
            return "EditStudentForm";
        }

        try {
            // Check if student exists
            if (!personsService.existsPersonById(student.getId())) {
                redirectAttributes.addFlashAttribute("error", "Person with ID " + student.getId() + " not found.");
                httpSession.removeAttribute("avatarStudent");
                return "redirect:/staff-home/students-list";
            }

            // Handle avatar upload
            if (avatarFile != null && !avatarFile.isEmpty()) {
                byte[] avatarBytes = avatarFile.getBytes();
                student.setAvatar(avatarBytes);
            } else {
                // Retain existing avatar
                Students existingStudent = studentsService.getStudentById(student.getId());
                student.setAvatar(existingStudent.getAvatar());
            }

            // Update student
            studentsService.updateStudent(student.getId(), student);

            // Handle parent accounts
            List<Student_ParentAccounts> currentParentLinks = parentAccountsService.getParentLinksByStudentId(student.getId());
            // Remove existing links and clean up unlinked parents
            for (Student_ParentAccounts link : currentParentLinks) {
                ParentAccounts parent = link.getParent();
                parentAccountsService.removeParentLink(link);
                // Check if parent is linked to any other students
                long linkedStudentsCount = parentAccountsService.countLinkedStudents(parent.getId(), student.getId());
                if (linkedStudentsCount == 0) {
                    // Remove parent account and its authenticator if no other students are linked
                    authenticatorsService.deleteAuthenticatorByPersonId(parent.getId());
                    parentAccountsService.deleteParent(parent);
                }
            }

            // Handle Parent 1
            if (isParent1InfoProvided && parent1 != null) {
                ParentAccounts existingParent = parentAccountsService.findByEmail(parent1.getEmail());
                RelationshipToStudent relationshipEnum1 = null;
                if (parentRelationship1 != null && !parentRelationship1.trim().isEmpty()) {
                    relationshipEnum1 = RelationshipToStudent.valueOf(parentRelationship1.toUpperCase());
                }
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
                }
                Student_ParentAccounts studentParent1 = new Student_ParentAccounts(
                        studentsService.getStudentById(student.getId()),
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
                RelationshipToStudent relationshipEnum2 = null;
                if (parentRelationship2 != null && !parentRelationship2.trim().isEmpty()) {
                    relationshipEnum2 = RelationshipToStudent.valueOf(parentRelationship2.toUpperCase());
                }
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
                }
                Student_ParentAccounts studentParent2 = new Student_ParentAccounts(
                        studentsService.getStudentById(student.getId()),
                        parent2,
                        staffsService.getStaff(),
                        LocalDateTime.now(),
                        relationshipEnum2,
                        supportPhoneNumber2
                );
                parentAccountsService.linkStudentToParent(studentParent2);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            httpSession.removeAttribute("avatarStudent");
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
            modelMap.addAttribute("parentLinks", parentAccountsService.getParentLinksByStudentId(student.getId()));
            // Keep parent input values
            modelMap.addAttribute("parentEmail1", parentEmail1);
            modelMap.addAttribute("supportPhoneNumber1", supportPhoneNumber1);
            modelMap.addAttribute("parentRelationship1", parentRelationship1);
            modelMap.addAttribute("parentEmail2", parentEmail2);
            modelMap.addAttribute("supportPhoneNumber2", supportPhoneNumber2);
            modelMap.addAttribute("parentRelationship2", parentRelationship2);
            return "EditStudentForm";
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Database error while updating student: " + e.getMessage());
            httpSession.removeAttribute("avatarStudent");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unexpected error while updating student: " + e.getMessage());
            httpSession.removeAttribute("avatarStudent");
        }

        return "redirect:/staff-home/students-list";
    }

    private boolean isEmailChanged(ParentAccounts newParent, ParentAccounts currentParent) {
        String newEmail = newParent.getEmail() != null ? newParent.getEmail().trim() : "";
        String currentEmail = currentParent.getEmail() != null ? currentParent.getEmail().trim() : "";
        return !newEmail.equals(currentEmail);
    }

    private void validateStudent(Students student, BindingResult bindingResult, MultipartFile avatarFile, List<String> errors) {
        // Annotation-based validation
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        // Custom validations
        if (!isValidName(student.getFirstName())) {
            errors.add("First name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (!isValidName(student.getLastName())) {
            errors.add("Last name is not valid. Only letters, spaces, and standard punctuation are allowed.");
        }

        if (student.getEmail() != null && !isValidEmail(student.getEmail())) {
            errors.add("Invalid email format.");
        }

        if (student.getPhoneNumber() != null && !isValidPhoneNumber(student.getPhoneNumber())) {
            errors.add("Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.");
        }

        if (student.getBirthDate() != null && student.getBirthDate().isAfter(LocalDate.now())) {
            errors.add("Date of birth must be in the past.");
        }

        // Validate avatar file
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.add("Avatar must be an image file.");
            }
            if (avatarFile.getSize() > 5 * 1024 * 1024) { // 5MB limit
                errors.add("Avatar file size must not exceed 5MB.");
            }
        }

        // Check for duplicate email/phone (excluding current student)
        if (student.getEmail() != null && personsService.existsByEmailExcludingId(student.getEmail(), student.getId())) {
            errors.add("The email address is already associated with another account.");
        }

        if (student.getPhoneNumber() != null && personsService.existsByPhoneNumberExcludingId(student.getPhoneNumber(), student.getId())) {
            errors.add("The phone number is already associated with another account.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true;
        }
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        return phoneNumber.matches(phoneRegex);
    }

    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}][\\p{L} .'-]{0,49}$";
        return name.matches(nameRegex);
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