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
        // Load current parent information if available
        Student_ParentAccounts parentLink = parentAccountsService.getParentLinkByStudentId(id);
        ParentAccounts currentParent = null;
        RelationshipToStudent currentRelationship = null;
        String currentSupportPhoneNumber = null;
        if (parentLink != null) {
            currentParent = parentLink.getParent();
            currentRelationship = parentLink.getRelationshipToStudent();
            currentSupportPhoneNumber = parentLink.getSupportPhoneNumber();
        }
        model.addAttribute("student", student);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
        model.addAttribute("currentParent", currentParent);
        model.addAttribute("currentRelationship", currentRelationship);
        model.addAttribute("currentSupportPhoneNumber", currentSupportPhoneNumber);
        return "EditStudentForm";
    }

    @PutMapping("/edit-student-form")
    public String updateStudent(
            @Valid @ModelAttribute("student") Students student,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "parentEmail", required = false) String parentEmail,
            @RequestParam(value = "supportPhoneNumber", required = false) String supportPhoneNumber,
            @RequestParam(value = "parentRelationship", required = false) String parentRelationship,
            RedirectAttributes redirectAttributes,
            ModelMap modelMap,
            HttpSession httpSession) {

        List<String> errors = new ArrayList<>();
        validateStudent(student, bindingResult, avatarFile, errors);

        // Validate parent if any meaningful field is provided
        ParentAccounts parent = null;
        boolean isParentInfoProvided = (parentEmail != null && !parentEmail.trim().isEmpty()) ||
                (supportPhoneNumber != null && !supportPhoneNumber.trim().isEmpty());

        if (isParentInfoProvided) {
            parent = new ParentAccounts();
            parent.setEmail(parentEmail);
            // Validate parent fields
            List<String> parentErrors = parentAccountsService.ParentValidation(parent);
            parentErrors.forEach(error -> errors.add("Parent: " + error));
        }

        // Validate relationship if provided
        if (parentRelationship != null && !parentRelationship.trim().isEmpty()) {
            try {
                RelationshipToStudent.valueOf(parentRelationship.toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("Parent: Invalid relationship to student. Allowed values: " +
                        String.join(", ", getRelationshipValues()));
            }
        }

        // Validate support phone number if provided
        if (supportPhoneNumber != null && !supportPhoneNumber.trim().isEmpty()) {
            if (!supportPhoneNumber.matches("^\\+?[0-9]{10,15}$")) {
                errors.add("Parent: Invalid support phone number format. Must be 10-15 digits, optionally starting with '+'.");
            }
        }

        // Prevent creating/updating parent account if only relationship or support phone number is provided
        if (isParentInfoProvided && parent != null) {
            boolean hasValidParentInfo = (parent.getEmail() != null && !parent.getEmail().trim().isEmpty());
            if (!hasValidParentInfo) {
                isParentInfoProvided = false; // Ignore parent info if no valid fields
            }
        }

        if (!errors.isEmpty()) {
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
            // Keep parent input values
            modelMap.addAttribute("parentEmail", parentEmail);
            modelMap.addAttribute("supportPhoneNumber", supportPhoneNumber);
            modelMap.addAttribute("parentRelationship", parentRelationship);
            // Load current parent info for display
            Student_ParentAccounts parentLink = parentAccountsService.getParentLinkByStudentId(student.getId());
            modelMap.addAttribute("currentParent", parentLink != null ? parentLink.getParent() : null);
            modelMap.addAttribute("currentRelationship", parentLink != null ? parentLink.getRelationshipToStudent() : null);
            modelMap.addAttribute("currentSupportPhoneNumber", parentLink != null ? parentLink.getSupportPhoneNumber() : null);
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

            // Handle parent account
            Student_ParentAccounts currentParentLink = parentAccountsService.getParentLinkByStudentId(student.getId());
            if (isParentInfoProvided && parent != null) {
                ParentAccounts existingParent = null;
                if (parent.getEmail() != null && !parent.getEmail().trim().isEmpty()) {
                    existingParent = parentAccountsService.findByEmail(parent.getEmail());
                }
                RelationshipToStudent relationshipEnum = null;
                if (parentRelationship != null && !parentRelationship.trim().isEmpty()) {
                    relationshipEnum = RelationshipToStudent.valueOf(parentRelationship.toUpperCase());
                }
                if (currentParentLink != null) {
                    ParentAccounts currentParent = currentParentLink.getParent();
                    // Check if parent is linked to other students
                    long linkedStudentsCount = parentAccountsService.countLinkedStudents(currentParent.getId(), student.getId());
                    if (linkedStudentsCount > 0 && isEmailChanged(parent, currentParent)) {
                        // Create new parent account if email changed and parent is linked to other students
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
                        // Update link to new parent
                        Student_ParentAccounts studentParent = new Student_ParentAccounts(
                                studentsService.getStudentById(student.getId()),
                                parent,
                                staffsService.getStaff(),
                                currentParentLink.getCreatedAt(),
                                relationshipEnum,
                                supportPhoneNumber
                        );
                        parentAccountsService.linkStudentToParent(studentParent);
                    } else {
                        // Update existing parent if no other students are linked or no change in email
                        currentParent.setEmail(parent.getEmail());
                        parentAccountsService.updateParent(currentParent);
                        // Update link with new relationship and support phone number
                        Student_ParentAccounts studentParent = new Student_ParentAccounts(
                                studentsService.getStudentById(student.getId()),
                                currentParent,
                                staffsService.getStaff(),
                                currentParentLink.getCreatedAt(),
                                relationshipEnum,
                                supportPhoneNumber
                        );
                        parentAccountsService.linkStudentToParent(studentParent);
                    }
                } else {
                    // No current parent, create new parent
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
                    // Create new link
                    Student_ParentAccounts studentParent = new Student_ParentAccounts(
                            studentsService.getStudentById(student.getId()),
                            parent,
                            staffsService.getStaff(),
                            LocalDateTime.now(),
                            relationshipEnum,
                            supportPhoneNumber
                    );
                    parentAccountsService.linkStudentToParent(studentParent);
                }
            } else if (parentEmail != null || supportPhoneNumber != null || parentRelationship != null) {
                // If any parent field is provided but invalid, keep existing link
            } else {
                // If no parent fields are provided, remove existing link
                if (currentParentLink != null) {
                    parentAccountsService.removeParentLink(currentParentLink);
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            httpSession.removeAttribute("avatarStudent");
        } catch (IOException e) {
            errors.add("Failed to process avatar: " + e.getMessage());
            modelMap.addAttribute("errors", errors);
            modelMap.addAttribute("genders", Arrays.asList(Gender.values()));
            modelMap.addAttribute("relationshipTypes", Arrays.asList(RelationshipToStudent.values()));
            // Keep parent input values
            modelMap.addAttribute("parentEmail", parentEmail);
            modelMap.addAttribute("supportPhoneNumber", supportPhoneNumber);
            modelMap.addAttribute("parentRelationship", parentRelationship);
            // Load current parent info for display
            Student_ParentAccounts parentLink = parentAccountsService.getParentLinkByStudentId(student.getId());
            modelMap.addAttribute("currentParent", parentLink != null ? parentLink.getParent() : null);
            modelMap.addAttribute("currentRelationship", parentLink != null ? parentLink.getRelationshipToStudent() : null);
            modelMap.addAttribute("currentSupportPhoneNumber", parentLink != null ? parentLink.getSupportPhoneNumber() : null);
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