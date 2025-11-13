// src/main/java/com/example/demo/learningProcess/controller/StaffLearningProcessController.java
package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import com.example.demo.students_Classes.abstractStudents_Class.service.StudentsClassesService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-learning-process")
public class StaffLearningProcessController {

    private final AcademicTranscriptsService academicTranscriptsService;
    private final StudentsService studentsService;
    private final ClassesService classesService;
    private final StudentsClassesService studentsClassesService;

    public StaffLearningProcessController(
            AcademicTranscriptsService academicTranscriptsService,
            StudentsService studentsService,
            ClassesService classesService,
            StudentsClassesService studentsClassesService) {
        this.academicTranscriptsService = academicTranscriptsService;
        this.studentsService = studentsService;
        this.classesService = classesService;
        this.studentsClassesService = studentsClassesService;
    }

    @PostMapping
    public String viewPost(@RequestParam String studentId, HttpSession session) {
        session.setAttribute("lp_studentId", studentId);
        return "redirect:/staff-learning-process";
    }

    @GetMapping
    public String listGet(HttpSession session, Model model) {
        return loadLearningProcess(session, model, 1, null);
    }

    @PostMapping(params = "page")
    public String listPostPage(
            @RequestParam int page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session,
            Model model) {
        return loadLearningProcess(session, model, page, pageSize);
    }

    @PostMapping(params = "pageSize")
    public String listPostPageSize(
            @RequestParam int pageSize,
            HttpSession session,
            Model model) {
        return loadLearningProcess(session, model, 1, pageSize);
    }

    @PostMapping("/detail-scores")
    public String detailScoresPost(
            @RequestParam String classId,
            HttpSession session,
            Model model) {

        String studentId = (String) session.getAttribute("lp_studentId");
        if (studentId == null) return "redirect:/staff-home/students-list";

        Students student = studentsService.getStudentById(studentId);
        if (student == null) {
            session.removeAttribute("lp_studentId");
            return "redirect:/staff-home/students-list";
        }

        Classes classes = classesService.findClassById(classId);
        if (classes == null) {
            model.addAttribute("transcripts", new ArrayList<>());
            model.addAttribute("className", "Unknown");
            return "StaffDetailScores";
        }

        List<?> transcripts = new ArrayList<>();
        String className = classes.getNameClass();

        if (classes instanceof MajorClasses mc) {
            transcripts = academicTranscriptsService.getAcademicTranscriptsByMajorClass(student, mc);
        } else if (classes instanceof MinorClasses mc) {
            transcripts = academicTranscriptsService.getAcademicTranscriptsByMinorClass(student, mc);
        } else if (classes instanceof SpecializedClasses sc) {
            transcripts = academicTranscriptsService.getAcademicTranscriptsBySpecializedClass(student, sc);
        }

        model.addAttribute("transcripts", transcripts);
        model.addAttribute("className", className);
        model.addAttribute("classId", classId);
        model.addAttribute("studentId", studentId);
        return "StaffDetailScores";
    }

    private String loadLearningProcess(HttpSession session, Model model, int page, Integer pageSize) {
        String studentId = (String) session.getAttribute("lp_studentId");
        if (studentId == null) return "redirect:/staff-home/students-list";

        Students student = studentsService.getStudentById(studentId);
        if (student == null) {
            session.removeAttribute("lp_studentId");
            return "redirect:/staff-home/students-list";
        }

        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("lp_pageSize");
            if (pageSize == null) pageSize = 10;
        }
        session.setAttribute("lp_pageSize", pageSize);

        List<Students_Classes> classes = studentsClassesService.getClassByStudent(student.getId());
        Long totalClasses = (long) classes.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, classes.size());
        List<Students_Classes> paginated = classes.subList(start, end);

        model.addAttribute("student", student);
        model.addAttribute("classes", paginated);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalClasses", totalClasses);

        return "StaffLearningProcess";
    }
}