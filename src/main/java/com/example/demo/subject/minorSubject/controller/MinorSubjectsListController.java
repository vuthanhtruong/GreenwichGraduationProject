package com.example.demo.subject.minorSubject.controller;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.syllabus.minorSyllabuses.model.MinorSyllabuses;
import com.example.demo.syllabus.minorSyllabuses.service.MinorSyllabusesService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home/minor-subjects-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorSubjectsListController {

    private final MinorSubjectsService subjectsService;
    private final MinorSyllabusesService syllabusesService;

    @Autowired
    public MinorSubjectsListController(MinorSubjectsService subjectsService, MinorSyllabusesService syllabusesService) {
        this.subjectsService = subjectsService;
        this.syllabusesService = syllabusesService;
    }

    @GetMapping("")
    public String showSubjectsList(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("subjectPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("subjectPageSize", pageSize);

            Long totalSubjects = subjectsService.numberOfSubjects();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("subjectPage", page);
            session.setAttribute("subjectTotalPages", totalPages);

            if (totalSubjects == 0) {
                model.addAttribute("subjects", new ArrayList<>());
                model.addAttribute("newSubject", new MinorSubjects());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalSubjects", 0);
                model.addAttribute("message", "No subjects found.");
                model.addAttribute("alertClass", "alert-warning");
                return "MinorSubjectsList";
            }

            int firstResult = (page - 1) * pageSize;
            List<MinorSubjects> subjects = subjectsService.getPaginatedSubjects(firstResult, pageSize);

            model.addAttribute("subjects", subjects);
            model.addAttribute("newSubject", new MinorSubjects());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", totalSubjects);
            return "MinorSubjectsList";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while retrieving subjects: " + e.getMessage()));
            model.addAttribute("newSubject", new MinorSubjects());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", 0);
            return "MinorSubjectsList";
        }
    }

    @PostMapping("/syllabuses-list/view-syllabus")
    public String viewSyllabusBySubject(@RequestParam("id") String subjectId,
                                        Model model,
                                        HttpSession session) {
        MinorSubjects subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errors", List.of("Subject not found"));
            return "redirect:/deputy-staff-home/minor-subjects-list";
        }

        // Lưu subjectId vào session để dùng ở trang danh sách syllabus
        session.setAttribute("currentMinorSubjectId", subjectId);

        // Load syllabus (page 1, size 10)
        int pageSize = 10;
        List<MinorSyllabuses> syllabuses = syllabusesService.getPaginatedSyllabuses(subjectId, 0, pageSize);
        Long total = syllabusesService.numberOfSyllabuses(subjectId);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        model.addAttribute("syllabuses", syllabuses.isEmpty() ? new ArrayList<>() : syllabuses);
        model.addAttribute("subject", subject);
        model.addAttribute("newSyllabus", new MinorSyllabuses());
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalSyllabuses", total);

        // Lưu page info vào session
        session.setAttribute("minorSyllabusPage", 1);
        session.setAttribute("minorSyllabusPageSize", pageSize);
        session.setAttribute("minorSyllabusTotalPages", totalPages);

        return "MinorSyllabusesList"; // → templates/MinorSyllabusesList.html
    }
}