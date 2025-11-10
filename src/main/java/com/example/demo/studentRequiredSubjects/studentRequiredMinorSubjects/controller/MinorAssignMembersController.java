package com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.controller;

import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredSubjectsId;
import com.example.demo.subject.abstractSubject.service.SubjectsService;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.service.StudentRequiredMinorSubjectsService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorAssignMembersController {

    private final MinorSubjectsService minorSubjectsService;
    private final StudentRequiredMinorSubjectsService minorRequiredService;
    private final DeputyStaffsService deputyStaffsService;
    private final StudentsService studentsService;
    private final SubjectsService subjectsService;

    public MinorAssignMembersController(
            MinorSubjectsService minorSubjectsService,
            StudentRequiredMinorSubjectsService minorRequiredService,
            DeputyStaffsService deputyStaffsService,
            StudentsService studentsService, SubjectsService subjectsService) {
        this.minorSubjectsService = minorSubjectsService;
        this.minorRequiredService = minorRequiredService;
        this.deputyStaffsService = deputyStaffsService;
        this.studentsService = studentsService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("/minor-study-plan/assign-members")
    public String assignMembers(HttpSession session, Model model, RedirectAttributes ra) {
        String subjectId = (String) session.getAttribute("currentMinorSubjectId");

        if (subjectId == null) {
            ra.addFlashAttribute("errorMessage", "Please select a subject first.");
            return "redirect:/deputy-staff-home/minor-study-plan";
        }

        MinorSubjects subject = minorSubjectsService.getSubjectById(subjectId);
        if (subject == null) {
            ra.addFlashAttribute("errorMessage", "Subject not found.");
            return "redirect:/deputy-staff-home/minor-study-plan";
        }

        model.addAttribute("subject", subject);
        model.addAttribute("assignedStudents", minorRequiredService.getStudentRequiredMinorSubjects(subject));
        model.addAttribute("availableStudents", minorRequiredService.getStudentsNotRequiredMinorSubject(subject));

        return "DeputyMinorAssignMembers";
    }

    @PostMapping("/minor-study-plan/assign/add")
    public String addStudents(
            @RequestParam String subjectId,
            @RequestParam(required = false) List<String> studentIds,
            HttpSession session,
            RedirectAttributes ra) {

        MinorSubjects subject = minorSubjectsService.getSubjectById(subjectId);
        if (subject == null || studentIds == null || studentIds.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Please select at least one student.");
            return "redirect:/deputy-staff-home/minor-study-plan/assign-members";
        }

        DeputyStaffs staff = deputyStaffsService.getDeputyStaff();
        if (staff == null) {
            ra.addFlashAttribute("errorMessage", "Deputy staff not found.");
            return "redirect:/deputy-staff-home/minor-study-plan/assign-members";
        }

        int addedCount = 0;
        for (String studentId : studentIds) {
            if (minorRequiredService.isStudentAlreadyRequiredForSubject(studentId, subjectId)) {
                continue;
            }

            Students student = studentsService.getStudentById(studentId);
            if (student == null) continue;

            StudentRequiredMinorSubjects assignment = new StudentRequiredMinorSubjects();
            assignment.setId(new StudentRequiredSubjectsId(studentId, subjectId));
            assignment.setStudent(student);
            assignment.setCreatedAt(LocalDateTime.now());
            assignment.setSubject(subjectsService.getSubjectById(subjectId));
            assignment.setMinorSubject(subject);
            assignment.setRequiredReason("Assigned by: " + staff.getFullName());
            assignment.setAssignedBy(staff);

            minorRequiredService.addStudentRequiredMinorSubject(assignment);
            addedCount++;
        }

        ra.addFlashAttribute("successMessage", "Successfully assigned " + addedCount + " student(s).");
        return "redirect:/deputy-staff-home/minor-study-plan/assign-members";
    }

    @PostMapping("/minor-study-plan/assign/remove")
    public String removeStudents(
            @RequestParam String subjectId,
            @RequestParam(required = false) List<String> studentIds,
            RedirectAttributes ra) {

        if (studentIds == null || studentIds.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Please select students to remove.");
            return "redirect:/deputy-staff-home/minor-study-plan/assign-members";
        }

        int removedCount = 0;
        for (String studentId : studentIds) {
            if (minorRequiredService.removeStudentRequiredMinorSubject(studentId, subjectId)) {
                removedCount++;
            }
        }

        ra.addFlashAttribute("successMessage", "Successfully removed " + removedCount + " assignment(s).");
        return "redirect:/deputy-staff-home/minor-study-plan/assign-members";
    }
}