package com.example.demo.studentRequiredSubjects.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import com.example.demo.studentRequiredSubjects.dao.StudentRequiredMajorSubjectsDAO;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredSubjectsId;
import com.example.demo.studentRequiredSubjects.service.StudentRequiredSubjectsService;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.subject.service.MajorSubjectsService;
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
public class ListMemberDesignationsController {

    private final MajorSubjectsService majorSubjectsService;
    private final StaffsService staffsService;
    private final StudentRequiredSubjectsService studentRequiredSubjectsService;
    private final StudentsService studentsService;

    @Autowired
    public ListMemberDesignationsController(MajorSubjectsService majorSubjectsService,
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
            @RequestParam(required = false) String learningProgramType,
            Model model,
            HttpSession session) {
        // Lưu vào session
        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentLearningProgramType", learningProgramType);

        // Redirect to GET to display the page
        return "redirect:/staff-home/study-plan/assign-members";
    }

    @GetMapping("/study-plan/assign-members")
    public String assignMembersGet(
            HttpSession session,
            Model model) {
        String subjectId = (String) session.getAttribute("currentSubjectId");
        String learningProgramType = (String) session.getAttribute("currentLearningProgramType");

        if (subjectId == null) {
            model.addAttribute("errorMessage", "Subject ID is required. Please select a subject first.");
            return "redirect:/staff-home/study-plan";
        }

        MajorSubjects subject = majorSubjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found.");
            model.addAttribute("LearningProgramTypes", LearningProgramTypes.values());
            model.addAttribute("learningProgramType", learningProgramType);
            return "FilterSubjects";
        }

        model.addAttribute("subject", subject);
        model.addAttribute("studentsNotRequired", studentRequiredSubjectsService.getStudentNotRequiredMajorSubjects(subject));
        model.addAttribute("studentRequiredSubjects", studentRequiredSubjectsService.getStudentRequiredMajorSubjects(subject));
        model.addAttribute("learningProgramType", learningProgramType);
        return "AssignMembers";
    }

    @PostMapping("/study-plan/assign-members/add")
    public String addSelectedStudents(
            @RequestParam("subjectId") String subjectId,
            @RequestParam("learningProgramType") String learningProgramType,
            @RequestParam("studentIds") List<String> studentIds,
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

        // Lưu session
        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentLearningProgramType", learningProgramType);

        int addedCount = 0;
        for (String studentId : studentIds) {
            Students student = studentsService.getStudentById(studentId);
            if (student != null) {
                // Kiểm tra xem student đã được assign chưa
                if (studentRequiredSubjectsService.isStudentAlreadyRequiredForSubject(studentId, subjectId)) {
                    continue; // Bỏ qua nếu đã tồn tại
                }

                // Tạo mới StudentRequiredMajorSubjects
                StudentRequiredMajorSubjects srm = new StudentRequiredMajorSubjects();
                srm.setId(new StudentRequiredSubjectsId(studentId, subjectId));
                srm.setStudent(student);
                srm.setSubject(subject);
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
            @RequestParam("learningProgramType") String learningProgramType,
            @RequestParam("studentIds") List<String> studentIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        // Lưu session
        session.setAttribute("currentSubjectId", subjectId);
        session.setAttribute("currentLearningProgramType", learningProgramType);

        int removedCount = 0;
        for (String studentId : studentIds) {
            if (studentRequiredSubjectsService.removeStudentRequiredMajorSubject(studentId, subjectId)) {
                removedCount++;
            }
        }

        if (removedCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage", removedCount + " student(s) removed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No students were removed.");
        }

        return "redirect:/staff-home/study-plan/assign-members";
    }
}