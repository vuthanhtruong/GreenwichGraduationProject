// src/main/java/com/example/demo/user/student/controller/ListStudentsController.java
package com.example.demo.user.student.controller;

import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.major.service.MajorsService;
import com.example.demo.specialization.service.SpecializationService;
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

import java.util.List;

@Controller
@RequestMapping("/staff-home")
public class ListStudentsController {

    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final CurriculumService curriculumService;
    private final SpecializationService specializationService;
    private final MajorsService majorsService;

    public ListStudentsController(
            StaffsService staffsService,
            StudentsService studentsService,
            CurriculumService curriculumService,
            SpecializationService specializationService, MajorsService majorsService) {
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.curriculumService = curriculumService;
        this.specializationService = specializationService;
        this.majorsService = majorsService;
    }

    @GetMapping("/students-list")
    public String listStudents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            Model model,
            HttpSession session) {

        try {
            String campusId = staffsService.getCampusOfStaff().getCampusId();
            String campusName = staffsService.getCampusOfStaff().getCampusName();
            String majorId = staffsService.getStaff().getMajorManagement().getMajorId(); // Fixed: Only staff's major

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("pageSize");
                if (pageSize == null) pageSize = 20;
            }
            session.setAttribute("pageSize", pageSize);
            session.setAttribute("currentPage", page);

            long totalStudents;
            List<Students> students;
            int totalPages;
            int firstResult = (page - 1) * pageSize;

            if (keyword != null && !keyword.trim().isBlank() && searchType != null) {
                totalStudents = studentsService.countSearchResultsByCampusAndMajor(
                        campusId, majorId, searchType, keyword.trim());
                students = studentsService.searchStudentsByCampusAndMajor(
                        campusId, majorId, searchType, keyword.trim(), firstResult, pageSize);
            } else {
                totalStudents = studentsService.totalStudentsByCampusAndMajor(campusId, majorId);
                students = studentsService.getPaginatedStudentsByCampusAndMajor(
                        campusId, majorId, firstResult, pageSize);
            }

            totalPages = (int) Math.ceil((double) totalStudents / pageSize);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            model.addAttribute("students", students);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("student", new Students());
            model.addAttribute("editStudent", new Students());
            model.addAttribute("relationshipTypes", RelationshipToStudent.values());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("currentCampusName", campusName);
            model.addAttribute("currentMajorId", majorId);
            model.addAttribute("currentMajorName", staffsService.getStaffMajor().getMajorName());
            model.addAttribute("specializations", specializationService.specializationsByMajor(majorsService.getMajorById(majorId)));

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