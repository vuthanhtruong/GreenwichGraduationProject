package com.example.demo.studentRequiredSubjects.controller;

import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.studentRequiredSubjects.dao.StudentRequiredMajorSubjectsDAO;
import com.example.demo.studentRequiredSubjects.service.StudentRequiredSubjectsService;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.studentRequiredSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.student.model.Students;
import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.staff.service.StaffsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListMemberDesignationsController {

    private final MajorSubjectsService majorSubjectsService;
    private final StaffsService staffsService;
    private final StudentRequiredSubjectsService studentRequiredSubjects;

    @Autowired
    public ListMemberDesignationsController(MajorSubjectsService majorSubjectsService, StaffsService staffsService, StudentRequiredSubjectsService studentRequiredSubjectsDAO, StudentRequiredSubjectsService studentRequiredSubjects) {
        this.majorSubjectsService = majorSubjectsService;
        this.staffsService = staffsService;
        this.studentRequiredSubjects = studentRequiredSubjects;
    }

    @PostMapping("/study-plan/assign-members")
    public String assignMembers(
            @RequestParam("id") String subjectId,
            @RequestParam(required = false) String learningProgramType,
            Model model) {
        MajorSubjects subject = majorSubjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found.");
            model.addAttribute("LearningProgramTypes", LearningProgramTypes.values());
            model.addAttribute("learningProgramType", learningProgramType);
            return "FilterSubjects";
        }

        model.addAttribute("subject", subject);
        model.addAttribute("studentsNotRequired", studentRequiredSubjects.getStudentNotRequiredMajorSubjects(subject));
        model.addAttribute("studentRequiredSubjects", studentRequiredSubjects.getStudentRequiredMajorSubjects(subject));
        model.addAttribute("learningProgramType", learningProgramType);
        return "AssignMembers";
    }
}