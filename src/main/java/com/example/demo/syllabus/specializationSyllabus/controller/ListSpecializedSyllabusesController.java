package com.example.demo.syllabus.specializationSyllabus.controller;

import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.syllabus.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.syllabus.specializationSyllabus.service.SpecializationSyllabusesService;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-subjects-list/syllabuses-list")
@PreAuthorize("hasRole('STAFF')")
public class ListSpecializedSyllabusesController {

    private final SpecializationSyllabusesService syllabusesService;
    private final SpecializedSubjectsService subjectsService;

    @Autowired
    public ListSpecializedSyllabusesController(SpecializationSyllabusesService syllabusesService, SpecializedSubjectsService subjectsService) {
        this.syllabusesService = syllabusesService;
        this.subjectsService = subjectsService;
    }

    @GetMapping("")
    public String listSyllabuses(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            String subjectId = (String) session.getAttribute("currentSubjectId");
            if (subjectId == null) {
                model.addAttribute("errors", List.of("No subject selected. Please select a subject."));
                model.addAttribute("newSyllabus", new SpecializationSyllabuses());
                model.addAttribute("subject", new SpecializedSubject());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", 5);
                model.addAttribute("totalSyllabuses", 0);
                return "SpecializedSyllabusesList";
            }

            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("syllabusPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("syllabusPageSize", pageSize);

            Long totalSyllabuses = syllabusesService.numberOfSyllabuses(subjectId);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSyllabuses / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("syllabusPage", page);
            session.setAttribute("syllabusTotalPages", totalPages);

            SpecializedSubject subject = subjectsService.getSubjectById(subjectId);
            if (totalSyllabuses == 0) {
                model.addAttribute("syllabuses", new ArrayList<>());
                model.addAttribute("newSyllabus", new SpecializationSyllabuses());
                model.addAttribute("subject", subject != null ? subject : new SpecializedSubject());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalSyllabuses", 0);
                model.addAttribute("message", "No syllabuses found for this subject.");
                model.addAttribute("alertClass", "alert-warning");
                return "SpecializedSyllabusesList";
            }

            int firstResult = (page - 1) * pageSize;
            List<SpecializationSyllabuses> syllabuses = syllabusesService.getPaginatedSyllabuses(subjectId, firstResult, pageSize);

            model.addAttribute("syllabuses", syllabuses);
            model.addAttribute("newSyllabus", new SpecializationSyllabuses());
            model.addAttribute("subject", subject != null ? subject : new SpecializedSubject());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSyllabuses", totalSyllabuses);
            return "SpecializedSyllabusesList";
        } catch (SecurityException e) {
            model.addAttribute("errors", List.of("Security error: " + e.getMessage()));
            model.addAttribute("newSyllabus", new SpecializationSyllabuses());
            model.addAttribute("subject", new SpecializedSubject());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSyllabuses", 0);
            return "SpecializedSyllabusesList";
        }
    }

    @PostMapping("/view-syllabus")
    public String viewSyllabusBySubject(@RequestParam("id") String subjectId, Model model, HttpSession session) {
        SpecializedSubject subject = subjectsService.getSubjectById(subjectId);
        if (subject == null) {
            model.addAttribute("errors", List.of("Subject not found"));
            return "redirect:/staff-home/specialized-subjects-list";
        }

        session.setAttribute("currentSubjectId", subjectId);
        List<SpecializationSyllabuses> syllabuses = syllabusesService.getPaginatedSyllabuses(subjectId, 0, 5);
        model.addAttribute("syllabuses", syllabuses.isEmpty() ? new ArrayList<>() : syllabuses);
        model.addAttribute("subject", subject);
        model.addAttribute("newSyllabus", new SpecializationSyllabuses());
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", Math.max(1, (int) Math.ceil((double) syllabusesService.numberOfSyllabuses(subjectId) / 5)));
        model.addAttribute("pageSize", 5);
        model.addAttribute("totalSyllabuses", syllabusesService.numberOfSyllabuses(subjectId));
        return "SpecializedSyllabusesList";
    }


    @GetMapping("/file/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getSyllabusFile(@PathVariable String id) {
        SpecializationSyllabuses syllabus = syllabusesService.getSyllabusById(id);
        if (syllabus != null && syllabus.getFileData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(syllabus.getFileType()))
                    .body(syllabus.getFileData());
        }
        return ResponseEntity.notFound().build();
    }
}