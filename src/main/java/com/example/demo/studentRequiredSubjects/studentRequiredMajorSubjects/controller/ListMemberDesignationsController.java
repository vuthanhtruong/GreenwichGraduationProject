package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.controller;

import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredSubjectsId;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.service.StudentRequiredSubjectsService;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
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
public class ListMemberDesignationsController {

    private final MajorSubjectsService majorSubjectsService;
    private final StaffsService staffsService;
    private final StudentRequiredSubjectsService studentRequiredSubjectsService;
    private final StudentsService studentsService;

    @Autowired
    public ListMemberDesignationsController(
            MajorSubjectsService majorSubjectsService,
            StaffsService staffsService,
            StudentRequiredSubjectsService studentRequiredSubjectsService,
            StudentsService studentsService) {
        this.majorSubjectsService = majorSubjectsService;
        this.staffsService = staffsService;
        this.studentRequiredSubjectsService = studentRequiredSubjectsService;
        this.studentsService = studentsService;
    }

    @PostMapping("/study-plan/assign-members")
    public String assignMembersPost(
            @RequestParam("id") String subjectId,
            @RequestParam(required = false) String curriculumId,
            HttpSession session) {
        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentCurriculumId", curriculumId);
        return "redirect:/staff-home/study-plan/assign-members";
    }

    @GetMapping("/study-plan/assign-members")
    public String assignMembersGet(
            HttpSession session,
            Model model) {
        String subjectId = (String) session.getAttribute("currentSubjectId");
        String curriculumId = (String) session.getAttribute("currentCurriculumId");

        if (subjectId == null) {
            model.addAttribute("errorMessage", "Subject ID is required. Please select a subject first.");
            return "redirect:/staff-home/study-plan";
        }

        MajorSubjects subject = majorSubjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found.");
            model.addAttribute("curriculumId", curriculumId);
            return "FilterSubjects";
        }

        model.addAttribute("subject", subject);
        model.addAttribute("studentsNotRequired", studentRequiredSubjectsService.getStudentNotRequiredMajorSubjects(subject));
        model.addAttribute("studentRequiredSubjects", studentRequiredSubjectsService.getStudentRequiredMajorSubjects(subject));
        model.addAttribute("curriculumId", curriculumId);
        return "AssignMembers";
    }

    @PostMapping("/study-plan/assign-members/add")
    public String addSelectedStudents(
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            @RequestParam(value = "studentIds", required = false) List<String> studentIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        MajorSubjects subject = majorSubjectsService.getSubjectById(subjectId);
        if (subject == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject not found.");
            return "redirect:/staff-home/study-plan/assign-members";
        }

        Staffs currentStaff = staffsService.getStaff();
        if (currentStaff == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Staff information not found.");
            return "redirect:/staff-home/study-plan/assign-members";
        }

        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentCurriculumId", curriculumId);

        if (studentIds == null || studentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No students selected to assign.");
            return "redirect:/staff-home/study-plan/assign-members";
        }

        int addedCount = 0;
        for (String studentId : studentIds) {
            Students student = studentsService.getStudentById(studentId);
            if (student != null && !studentRequiredSubjectsService.isStudentAlreadyRequiredForSubject(studentId, subjectId)) {
                StudentRequiredMajorSubjects srm = new StudentRequiredMajorSubjects();
                srm.setId(new StudentRequiredSubjectsId(studentId, subjectId));
                srm.setStudent(student);
                srm.setSubject(subject);
                srm.setMajorSubject(subject);
                srm.setRequiredReason("Assigned by staff: " + currentStaff.getFullName());
                srm.setAssignedBy(currentStaff);
                srm.setCreatedAt(java.time.LocalDateTime.now());

                studentRequiredSubjectsService.addStudentRequiredMajorSubject(srm);
                addedCount++;
            }
        }

        if (addedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", addedCount + " student(s) assigned successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No new students were assigned. Check if they are already assigned.");
        }

        return "redirect:/staff-home/study-plan/assign-members";
    }

    @PostMapping("/study-plan/assign-members/remove")
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
            return "redirect:/staff-home/study-plan/assign-members";
        }

        int removedCount = 0;
        for (String studentId : studentIds) {
            if (studentRequiredSubjectsService.removeStudentRequiredMajorSubject(studentId, subjectId)) {
                removedCount++;
            }
        }

        if (removedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", removedCount + " student(s) removed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No students were removed. Check if they are assigned.");
        }

        return "redirect:/staff-home/study-plan/assign-members";
    }
}