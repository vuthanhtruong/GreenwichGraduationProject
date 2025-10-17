package com.example.demo.AcademicTranscript.controller;

import com.example.demo.AcademicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.abstractClass.model.Classes;
import com.example.demo.classes.abstractClass.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student-home/learning-process")
public class LearningProcessController {

    private final AcademicTranscriptsService academicTranscriptsService;
    private final StudentsService studentsService;
    private final ClassesService classesService;

    public LearningProcessController(AcademicTranscriptsService academicTranscriptsService,
                                     StudentsService studentsService,
                                     ClassesService classesService) {
        this.academicTranscriptsService = academicTranscriptsService;
        this.studentsService = studentsService;
        this.classesService = classesService;
    }

    @GetMapping("")
    public String listLearningProcess(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            // Lấy thông tin học sinh hiện tại
            Students student = studentsService.getStudent();
            if (student == null) {
                model.addAttribute("errors", List.of("Student not found"));
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 10);
                model.addAttribute("totalClasses", 0);
                return "LearningProcess";
            }

            // Xác định pageSize
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("learningProcessPageSize");
                if (pageSize == null) {
                    pageSize = 10; // Mặc định 10 lớp/trang
                }
            }
            session.setAttribute("learningProcessPageSize", pageSize);

            // Lấy danh sách lớp học
            List<Students_Classes> classes = academicTranscriptsService.getLearningProcess(student);
            Long totalClasses = (long) classes.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("learningProcessPage", page);
            session.setAttribute("learningProcessTotalPages", totalPages);

            if (totalClasses == 0) {
                model.addAttribute("classes", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalClasses", 0);
                model.addAttribute("alertClass", "alert-warning");
                return "LearningProcess";
            }

            // Áp dụng phân trang thủ công
            int firstResult = (page - 1) * pageSize;
            int lastResult = Math.min(firstResult + pageSize, classes.size());
            List<Students_Classes> paginatedClasses = classes.subList(firstResult, lastResult);

            model.addAttribute("classes", paginatedClasses);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalClasses", totalClasses);
            return "LearningProcess";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Error fetching classes: " + e.getMessage()));
            model.addAttribute("classes", new ArrayList<>());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", 10);
            model.addAttribute("totalClasses", 0);
            return "LearningProcess";
        }
    }

    @GetMapping("/detail-scores")
    public String detailScores(@RequestParam String classId, Model model) {
        try {
            // Lấy thông tin sinh viên hiện tại
            Students student = studentsService.getStudent();
            Classes classes = classesService.findClassById(classId);
            if (classes == null) {
                model.addAttribute("errors", List.of("Class not found for ID: " + classId));
                model.addAttribute("transcripts", new ArrayList<>());
                model.addAttribute("className", "N/A");
                model.addAttribute("classId", classId);
                return "DetailScores";
            }

            // Xác định loại lớp và lấy bảng điểm
            List<?> transcripts = new ArrayList<>();
            String className = "N/A";
            if (classes instanceof MajorClasses majorClass) {
                transcripts = academicTranscriptsService.getAcademicTranscriptsByMajorClass(student, majorClass);
                className = majorClass.getNameClass();
            } else if (classes instanceof MinorClasses minorClass) {
                transcripts = academicTranscriptsService.getAcademicTranscriptsByMinorClass(student, minorClass);
                className = minorClass.getNameClass();
            } else if (classes instanceof SpecializedClasses specializedClass) {
                transcripts = academicTranscriptsService.getAcademicTranscriptsBySpecializedClass(student, specializedClass);
                className = specializedClass.getNameClass();
            }

            model.addAttribute("transcripts", transcripts);
            model.addAttribute("className", className);
            model.addAttribute("classId", classId);
            return "DetailScores";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Error fetching transcripts: " + e.getMessage()));
            model.addAttribute("transcripts", new ArrayList<>());
            model.addAttribute("className", "N/A");
            model.addAttribute("classId", classId);
            return "DetailScores";
        }
    }
}