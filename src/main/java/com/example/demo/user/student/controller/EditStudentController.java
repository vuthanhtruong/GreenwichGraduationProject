package com.example.demo.user.student.controller;

import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.entity.Enums.Gender;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.specialization.service.SpecializationService;
import com.example.demo.user.parentAccount.model.Student_ParentAccounts;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.person.service.PersonsService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/students-list")
public class EditStudentController {

    private final StudentsService studentsService;
    private final PersonsService personsService;
    private final CurriculumService curriculumService;
    private final StaffsService staffsService;
    private final SpecializationService specializationService;
    private final ParentAccountsService parentAccountsService;

    public EditStudentController(StudentsService studentsService,
                                 PersonsService personsService,
                                 CurriculumService curriculumService,
                                 StaffsService staffsService,
                                 SpecializationService specializationService,
                                 ParentAccountsService parentAccountsService) {
        this.studentsService = studentsService;
        this.personsService = personsService;
        this.curriculumService = curriculumService;
        this.staffsService = staffsService;
        this.specializationService = specializationService;
        this.parentAccountsService = parentAccountsService;
    }
    @GetMapping("/edit-student-form")
    public String showEditFormGet(             // Chỉ dùng lần đầu
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            @SessionAttribute(value = "editStudentId", required = false) String sessionStudentId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Students student = studentsService.getStudentById(session.getAttribute("studentId").toString());
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Học sinh không tồn tại.");
            session.removeAttribute("editStudentId");
            return "redirect:/staff-home/students-list";
        }

        List<Student_ParentAccounts> parentLinks = parentAccountsService.getParentLinksByStudentId(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("parentLinks", parentLinks);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        model.addAttribute("source", source);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));

        return "EditStudentForm"; // Tên template
    }

    @PostMapping("/edit-student-form")
    public String showEditForm(
            @RequestParam String id,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            Model model,
            RedirectAttributes redirectAttributes) {

        Students student = studentsService.getStudentById(id);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Student with ID " + id + " not found.");
            return "search".equals(source)
                    ? "redirect:/staff-home/search-students"
                    : "redirect:/staff-home/students-list";
        }

        // Load parents linked to this student
        List<Student_ParentAccounts> parentLinks = parentAccountsService.getParentLinksByStudentId(id);

        model.addAttribute("student", student);
        model.addAttribute("parentLinks", parentLinks);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        model.addAttribute("source", source);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));

        return "EditStudentForm";
    }

    @PutMapping("/edit-student-form")  // Changed to POST (HTML form doesn't support PUT)
    public String editStudent(
            @Valid @ModelAttribute("student") Students student,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            @RequestParam("specializationId") String specializationId,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession httpSession) {

        Map<String, String> errors = new HashMap<>();
        errors.putAll(studentsService.StudentValidation(student, avatarFile));

        if (!errors.isEmpty()) {
            model.addAttribute("curriculumId", curriculumId);
            model.addAttribute("specializationId", specializationId);
            model.addAttribute("errors", errors);
            model.addAttribute("genders", Arrays.asList(Gender.values()));
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            model.addAttribute("source", source);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            httpSession.setAttribute("avatarStudent", "/staff-home/students-list/avatar/" + student.getId());
            return "EditStudentForm";
        }

        try {
            if (!personsService.existsPersonById(student.getId())) {
                redirectAttributes.addFlashAttribute("error", "Student not found.");
                httpSession.removeAttribute("avatarStudent");
                return "search".equals(source)
                        ? "redirect:/staff-home/search-students"
                        : "redirect:/staff-home/students-list";
            }

            if (avatarFile != null && !avatarFile.isEmpty()) {
                student.setAvatar(avatarFile.getBytes());
            } else {
                Students existing = studentsService.getStudentById(student.getId());
                student.setAvatar(existing.getAvatar());
            }

            if (curriculumId != null && !curriculumId.isEmpty()) {
                student.setCurriculum(curriculumService.getCurriculumById(curriculumId));
            }

            Specialization specialization = specializationService.getSpecializationById(specializationId);
            student.setSpecialization(specialization);

            studentsService.editStudent(student.getId(), student.getCurriculum(), specialization, student);

            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            httpSession.removeAttribute("avatarStudent");

            if ("search".equals(source)) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                return "redirect:/staff-home/search-students";
            }

            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            return "redirect:/staff-home/students-list";

        } catch (IOException e) {
            errors.put("general", "Failed to process avatar: " + e.getMessage());
        } catch (DataAccessException e) {
            errors.put("general", "Database error: " + e.getMessage());
        } catch (Exception e) {
            errors.put("general", "Unexpected error: " + e.getMessage());
        }

        model.addAttribute("errors", errors);
        model.addAttribute("curriculumId", curriculumId);
        model.addAttribute("specializationId", specializationId);
        model.addAttribute("genders", Arrays.asList(Gender.values()));
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
        httpSession.setAttribute("avatarStudent", "/staff-home/students-list/avatar/" + student.getId());
        return "EditStudentForm";
    }
}