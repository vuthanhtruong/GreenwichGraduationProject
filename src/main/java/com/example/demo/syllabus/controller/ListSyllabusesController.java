package com.example.demo.syllabus.controller;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.syllabus.model.Syllabuses;
import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.syllabus.service.SyllabusesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/staff-home")
@PreAuthorize("hasRole('STAFF')")
public class ListSyllabusesController {

    private final SyllabusesService syllabusesService;
    private final MajorSubjectsService subjectsService;

    @Autowired
    public ListSyllabusesController(SyllabusesService syllabusesService, MajorSubjectsService subjectsService) {
        this.syllabusesService = syllabusesService;
        this.subjectsService = subjectsService;
    }
    @GetMapping("/major-subjects-list/syllabuses-list")
    public String showSyllabusForm(Model model, HttpSession session) {
        String subjectId = (String) session.getAttribute("currentSubjectId");
        model.addAttribute("newSyllabus", new Syllabuses());
        if (subjectId != null) {
            MajorSubjects subject = subjectsService.getSubjectById(subjectId);
            model.addAttribute("subject", subject != null ? subject : new MajorSubjects());
            model.addAttribute("syllabuses", subject != null ? syllabusesService.getSyllabusesBySubject(subject) : null);
        } else {
            model.addAttribute("subject", new MajorSubjects());
            model.addAttribute("errorMessage", "No subject selected. Please select a subject from the subjects list.");
        }
        return "SyllabusesList";
    }

    @PostMapping("/major-subjects-list/view-syllabus")
    public String viewSyllabusBySubject(@RequestParam("id") String subjectId, Model model, HttpSession session) {
        MajorSubjects subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found");
            return "redirect:/staff-home/major-subjects-list";
        }
        // Lưu subjectId vào session
        session.setAttribute("currentSubjectId", subjectId);
        List<Syllabuses> syllabuses = syllabusesService.getSyllabusesBySubject(subject);
        model.addAttribute("syllabuses", syllabuses.isEmpty() ? null : syllabuses);
        model.addAttribute("subject", subject);
        model.addAttribute("newSyllabus", new Syllabuses());
        return "SyllabusesList";
    }
}