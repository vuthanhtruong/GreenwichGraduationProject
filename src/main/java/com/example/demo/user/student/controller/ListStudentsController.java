package com.example.demo.user.student.controller;

import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.entity.Enums.RelationshipToStudent;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
public class ListStudentsController {
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final CurriculumService curriculumService;
    private final SpecializationService specializationService;

    public ListStudentsController(StaffsService staffsService, StudentsService studentsService, CurriculumService curriculumService, SpecializationService specializationService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.curriculumService = curriculumService;
        this.specializationService = specializationService;
    }

    @GetMapping("/students-list")
    public String listStudents(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("pageSize", pageSize);
            session.setAttribute("currentPage", page);

            Long totalStudents = studentsService.numberOfStudents();

            if (totalStudents == 0) {
                model.addAttribute("students", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("student", new Students());
                model.addAttribute("editStudent", new Students());
                model.addAttribute("relationshipTypes", RelationshipToStudent.values());
                model.addAttribute("curriculums", curriculumService.getCurriculums());
                model.addAttribute("specializations",specializationService.specializationsByMajor(staffsService.getStaffMajor()));
                return "StudentsList";
            }

            int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages) page = totalPages;

            int firstResult = (page - 1) * pageSize;

            List<Students> students = studentsService.getPaginatedStudents(firstResult, pageSize);

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("student", new Students());
            model.addAttribute("editStudent", new Students());
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations",specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "StudentsList";
        } catch (SecurityException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/students-list/avatar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getStudentAvatar(@PathVariable String id) {
        Students student = studentsService.getStudentById(id);
        if (student != null && student.getAvatar() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(student.getAvatar());
        }
        return ResponseEntity.notFound().build();
    }
}