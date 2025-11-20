// src/main/java/com/example/demo/learningProcess/controller/ParentLearningProcessController.java
package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
import com.example.demo.students_Classes.abstractStudents_Class.service.StudentsClassesService;
import com.example.demo.user.parentAccount.service.ParentAccountsService;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/parent")
public class ParentLearningProcessController {

    private final ParentAccountsService parentAccountsService;
    private final StudentsClassesService studentsClassesService;
    private final ClassesService classesService;
    private final AcademicTranscriptsService academicTranscriptsService;

    public ParentLearningProcessController(ParentAccountsService parentAccountsService,
                                           StudentsClassesService studentsClassesService,
                                           ClassesService classesService,
                                           AcademicTranscriptsService academicTranscriptsService) {
        this.parentAccountsService = parentAccountsService;
        this.studentsClassesService = studentsClassesService;
        this.classesService = classesService;
        this.academicTranscriptsService = academicTranscriptsService;
    }

    @PostMapping("/learning-process")
    public String selectChild(
            @RequestParam String studentId,
            HttpSession session) {

        var parent = parentAccountsService.getParent();
        boolean valid = parentAccountsService.getStudentsByParentId(parent.getId()).stream()
                .anyMatch(s -> s.getId().equals(studentId));

        if (!valid) {
            return "redirect:/parent/learning-process?error";
        }

        session.setAttribute("lp_childId", studentId);
        session.removeAttribute("lp_pageSize"); // reset phân trang khi đổi con
        return "redirect:/parent/learning-process/view";
    }

    // ===================== 3. Trang chính Learning Process (phân trang) =====================
    @GetMapping("/learning-process/view")
    public String viewLearningProcess(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session,
            Model model) {

        String childId = (String) session.getAttribute("lp_childId");
        if (childId == null) {
            return "redirect:/parent/learning-process";
        }

        var parent = parentAccountsService.getParent();
        Students child = parentAccountsService.getStudentsByParentId(parent.getId()).stream()
                .filter(s -> s.getId().equals(childId))
                .findFirst()
                .orElse(null);

        if (child == null) {
            session.removeAttribute("lp_childId");
            return "redirect:/parent/learning-process";
        }

        if (pageSize == null) {
            pageSize = (Integer) session.getAttribute("lp_pageSize");
            if (pageSize == null) pageSize = 10;
        }
        session.setAttribute("lp_pageSize", pageSize);

        List<Students_Classes> allClasses = studentsClassesService.getClassByStudent(child.getId());
        long totalClasses = allClasses.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalClasses / pageSize));
        page = Math.max(1, Math.min(page, totalPages));

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allClasses.size());
        List<Students_Classes> paginated = allClasses.subList(start, end);

        model.addAttribute("student", child);
        model.addAttribute("classes", paginated);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalClasses", totalClasses);

        // Cho phép chuyển con nhanh
        model.addAttribute("siblings", parentAccountsService.getStudentsByParentId(parent.getId()));
        model.addAttribute("currentChildId", childId);

        return "ParentLearningProcess"; // HTML riêng cho phụ huynh
    }

    // ===================== 4. Xem chi tiết điểm lớp học =====================
    @GetMapping("/learning-process/detail-scores")
    public String detailScores(
            @RequestParam String classId,
            HttpSession session,
            Model model) {

        String childId = (String) session.getAttribute("lp_childId");
        if (childId == null) {
            return "redirect:/parent/learning-process";
        }

        var parent = parentAccountsService.getParent();
        Students child = parentAccountsService.getStudentsByParentId(parent.getId()).stream()
                .filter(s -> s.getId().equals(childId))
                .findFirst()
                .orElse(null);

        if (child == null) {
            return "redirect:/parent/learning-process";
        }

        Classes classes = classesService.findClassById(classId);
        if (classes == null) {
            model.addAttribute("transcripts", new ArrayList<>());
            model.addAttribute("className", "Unknown Class");
            model.addAttribute("student", child);
            return "ParentDetailScores";
        }

        List<?> transcripts = new ArrayList<>();
        String className = classes.getNameClass();

        if (classes instanceof com.example.demo.classes.majorClasses.model.MajorClasses mc) {
            transcripts = academicTranscriptsService.getAcademicTranscriptsByMajorClass(child, mc);
        } else if (classes instanceof com.example.demo.classes.minorClasses.model.MinorClasses mc) {
            transcripts = academicTranscriptsService.getAcademicTranscriptsByMinorClass(child, mc);
        } else if (classes instanceof com.example.demo.classes.specializedClasses.model.SpecializedClasses sc) {
            transcripts = academicTranscriptsService.getAcademicTranscriptsBySpecializedClass(child, sc);
        }

        model.addAttribute("transcripts", transcripts);
        model.addAttribute("className", className);
        model.addAttribute("classId", classId);
        model.addAttribute("student", child);
        model.addAttribute("currentChildId", childId);
        model.addAttribute("siblings", parentAccountsService.getStudentsByParentId(parent.getId()));

        return "ParentDetailScores"; // HTML riêng cho phụ huynh
    }
}