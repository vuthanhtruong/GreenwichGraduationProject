package com.example.demo.controller.Read;

import com.example.demo.entity.Subjects;
import com.example.demo.entity.Syllabuses;
import com.example.demo.service.SubjectsService;
import com.example.demo.service.SyllabusesService;
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
    private final SubjectsService subjectsService;

    @Autowired
    public ListSyllabusesController(SyllabusesService syllabusesService, SubjectsService subjectsService) {
        this.syllabusesService = syllabusesService;
        this.subjectsService = subjectsService;
    }
    @GetMapping("/major-subjects-list/syllabuses-list")
    public String showSyllabusForm(Model model, HttpSession session) {
        String subjectId = (String) session.getAttribute("currentSubjectId");
        model.addAttribute("newSyllabus", new Syllabuses());
        if (subjectId != null) {
            Subjects subject = subjectsService.getSubjectById(subjectId);
            model.addAttribute("subject", subject != null ? subject : new Subjects());
            model.addAttribute("syllabuses", subject != null ? syllabusesService.getSyllabusesBySubject(subject) : null);
        } else {
            model.addAttribute("subject", new Subjects());
            model.addAttribute("errorMessage", "No subject selected. Please select a subject from the subjects list.");
        }
        return "SyllabusesList";
    }

    @PostMapping("/major-subjects-list/view-syllabus")
    public String viewSyllabusBySubject(@RequestParam("id") String subjectId, Model model, HttpSession session) {
        Subjects subject = subjectsService.getSubjectById(subjectId);
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