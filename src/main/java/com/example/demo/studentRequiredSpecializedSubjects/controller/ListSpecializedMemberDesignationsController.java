package com.example.demo.studentRequiredSpecializedSubjects.controller;

import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import com.example.demo.studentRequiredSpecializedSubjects.model.StudentRequiredSpecializedSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredSubjectsId;
import com.example.demo.studentRequiredSpecializedSubjects.service.StudentRequiredSpecializedSubjectsService;
import com.example.demo.specializedSubject.model.SpecializedSubject;
import com.example.demo.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSpecializedMemberDesignationsController {

    private final SpecializedSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final StudentRequiredSpecializedSubjectsService studentRequiredSubjectsService;
    private final StudentsService studentsService;

    @Autowired
    public ListSpecializedMemberDesignationsController(
            SpecializedSubjectsService subjectsService,
            StaffsService staffsService,
            StudentRequiredSpecializedSubjectsService  studentRequiredSubjectsService,
            StudentsService studentsService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.studentRequiredSubjectsService = studentRequiredSubjectsService;
        this.studentsService = studentsService;
    }

    @PostMapping("/specialized-study-plan/assign-members")
    public String assignMembersPost(
            @RequestParam("id") String subjectId,
            @RequestParam(required = false) String curriculumId,
            HttpSession session) {
        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentCurriculumId", curriculumId);
        return "redirect:/staff-home/specialized-study-plan/assign-members";
    }

    @GetMapping("/specialized-study-plan/assign-members")
    public String assignMembersGet(
            HttpSession session,
            Model model) {
        String subjectId = (String) session.getAttribute("currentSubjectId");
        String curriculumId = (String) session.getAttribute("currentCurriculumId");

        if (subjectId == null) {
            model.addAttribute("errorMessage", "Subject ID is required. Please select a subject first.");
            return "redirect:/staff-home/specialized-study-plan";
        }

        SpecializedSubject subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found.");
            model.addAttribute("curriculumId", curriculumId);
            return "FilterSpecializedSubjects";
        }

        model.addAttribute("subject", subject);
        model.addAttribute("studentsNotRequired", studentRequiredSubjectsService.getStudentNotRequiredSpecializedSubjects(subject));
        model.addAttribute("studentRequiredSubjects", studentRequiredSubjectsService.getStudentRequiredSpecializedSubjects(subject));
        model.addAttribute("curriculumId", curriculumId);
        return "AssignSpecializedMembers";
    }

    @PostMapping("/specialized-study-plan/assign-members/add")
    public String addSelectedStudents(
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            @RequestParam(value = "studentIds", required = false) List<String> studentIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        SpecializedSubject subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject not found.");
            return "redirect:/staff-home/specialized-study-plan/assign-members";
        }

        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Staff information not found.");
            return "redirect:/staff-home/specialized-study-plan/assign-members";
        }

        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentCurriculumId", curriculumId);

        if (studentIds == null || studentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No students selected to assign.");
            return "redirect:/staff-home/specialized-study-plan/assign-members";
        }

        int addedCount = 0;
        for (String studentId : studentIds) {
            Students student = studentsService.getStudentById(studentId);
            if (student != null && !studentRequiredSubjectsService.isStudentAlreadyRequiredForSpecializedSubject(studentId, subjectId)) {
                StudentRequiredSpecializedSubjects srs = new StudentRequiredSpecializedSubjects();
                srs.setId(new StudentRequiredSubjectsId(studentId, subjectId));
                srs.setStudent(student);
                srs.setSubject(subject);
                srs.setSpecializedSubject(subject);
                srs.setRequiredReason("Assigned by staff: " + currentStaff.getFullName());
                srs.setAssignedBy(currentStaff);
                srs.setCreatedAt(java.time.LocalDateTime.now());

                studentRequiredSubjectsService.addStudentRequiredSpecializedSubject(srs);
                addedCount++;
            }
        }

        if (addedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", addedCount + " student(s) assigned successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No new students were assigned. Check if they are already assigned.");
        }

        return "redirect:/staff-home/specialized-study-plan/assign-members";
    }

    @PostMapping("/specialized-study-plan/assign-members/remove")
    public String removeSelectedStudents(
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            @RequestParam(value = "studentIds", required = false) List<String> studentIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentCurriculumId", curriculumId);

        if (studentIds == null || studentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No students selected to remove.");
            return "redirect:/staff-home/specialized-study-plan/assign-members";
        }

        int removedCount = 0;
        for (String studentId : studentIds) {
            if (studentRequiredSubjectsService.removeStudentRequiredSpecializedSubject(studentId, subjectId)) {
                removedCount++;
            }
        }

        if (removedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", removedCount + " student(s) removed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No students were removed. Check if they are assigned.");
        }

        return "redirect:/staff-home/specialized-study-plan/assign-members";
    }
}