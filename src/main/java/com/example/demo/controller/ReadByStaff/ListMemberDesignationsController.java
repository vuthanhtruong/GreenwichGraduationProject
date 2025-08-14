package com.example.demo.controller.ReadByStaff;

import com.example.demo.dao.StudentRequiredMajorSubjectsDAO;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.AbstractClasses.StudentRequiredSubjects;
import com.example.demo.entity.StudentRequiredMajorSubjects;
import com.example.demo.entity.Students;
import com.example.demo.service.MajorSubjectsService;
import com.example.demo.service.StaffsService;
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
    private final StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO;

    @Autowired
    public ListMemberDesignationsController(MajorSubjectsService majorSubjectsService, StaffsService staffsService, StudentRequiredMajorSubjectsDAO studentRequiredSubjectsDAO) {
        this.majorSubjectsService = majorSubjectsService;
        this.staffsService = staffsService;
        this.studentRequiredSubjectsDAO = studentRequiredSubjectsDAO;
    }

    @PostMapping("/study-plan/assign-members")
    public String assignMembersForm(@RequestParam("id") String subjectId, Model model) {
        MajorSubjects subject = majorSubjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found");
            return "redirect:/staff-home/study-plan";
        }
        List<Students> studentsNotRequired = studentRequiredSubjectsDAO.getStudentNotRequiredMajorSubjects(subject);
        List<StudentRequiredMajorSubjects>  studentRequiredSubjects = studentRequiredSubjectsDAO.getStudentRequiredMajorSubjects(subject);
        model.addAttribute("subject", subject);
        model.addAttribute("studentsNotRequired", studentsNotRequired);
        model.addAttribute("studentsNotRequired", studentRequiredSubjects);
        // Add other necessary attributes, e.g., required students if needed
        return "AssignMembers"; // Redirect to a new view for assigning members
    }
}