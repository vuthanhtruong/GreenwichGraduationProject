package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.model.SpecializedAcademicTranscripts;
import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.students_Classes.students_SpecializedClasses.service.StudentsSpecializedClassesService;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home/specialized-classes-list")
@PreAuthorize("hasRole('STAFF')")
public class SpecializedTranscriptEntryController {

    private final SpecializedClassesService specializedClassesService;
    private final StudentsSpecializedClassesService studentsSpecializedService;
    private final AcademicTranscriptsService transcriptsService;
    private final StaffsService staffsService;

    public SpecializedTranscriptEntryController(
            SpecializedClassesService specializedClassesService,
            StudentsSpecializedClassesService studentsSpecializedService,
            AcademicTranscriptsService transcriptsService,
            StaffsService staffsService) {

        this.specializedClassesService = specializedClassesService;
        this.studentsSpecializedService = studentsSpecializedService;
        this.transcriptsService = transcriptsService;
        this.staffsService = staffsService;
    }

    @PostMapping("/enter-transcript")
    public String loadTranscriptPost(@RequestParam("classId") String classId,
                                     Model model, RedirectAttributes ra, HttpSession session) {
        return loadPage(classId, model, ra, session);
    }

    @GetMapping("/enter-transcript")
    public String loadTranscriptGet(@RequestParam(value = "classId", required = false) String classId,
                                    Model model, HttpSession session, RedirectAttributes ra) {
        if (classId == null || classId.isBlank()) {
            classId = (String) session.getAttribute("currentSpecializedClassId");
        }
        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("errorMessage", "Class ID is required.");
            return "redirect:/staff-home/specialized-classes-list";
        }
        return loadPage(classId, model, ra, session);
    }

    private String loadPage(String classId, Model model, RedirectAttributes ra, HttpSession session) {
        SpecializedClasses clazz = specializedClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found!");
            return "redirect:/staff-home/specialized-classes-list";
        }

        List<Students> students = studentsSpecializedService.getStudentsByClass(clazz);
        if (students.isEmpty()) {
            model.addAttribute("warningMessage", "No students enrolled in this class.");
        }

        List<SpecializedAcademicTranscripts> transcripts = transcriptsService.getTranscriptsByClass(clazz);

        Map<String, SpecializedAcademicTranscripts> transcriptMap = transcripts.stream()
                .collect(Collectors.toMap(
                        t -> t.getStudent().getId(),
                        t -> t,
                        (a, b) -> a
                ));

        model.addAttribute("clazz", clazz);
        model.addAttribute("students", students);
        model.addAttribute("transcriptMap", transcriptMap);

        session.setAttribute("currentSpecializedClassId", classId);

        return "SpecializedEnterTranscript";
    }

    @PostMapping("/save-transcript")
    @Transactional
    public String saveTranscript(@RequestParam("classId") String classId,
                                 @RequestParam Map<String, String> allParams,
                                 RedirectAttributes ra) {

        SpecializedClasses clazz = specializedClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found!");
            return "redirect:/staff-home/specialized-classes-list";
        }

        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            ra.addFlashAttribute("errorMessage", "Staff not found!");
            return "redirect:/staff-home/specialized-classes-list";
        }

        List<Students> students = studentsSpecializedService.getStudentsByClass(clazz);
        if (students.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "No students to grade.");
            return "redirect:/staff-home/specialized-classes-list/enter-transcript";
        }

        List<String> errors = new ArrayList<>();
        int saved = 0;

        for (Students s : students) {
            String sid = s.getId();
            String p = sid + "_";

            String c1 = allParams.get("c1_" + p);
            String c2 = allParams.get("c2_" + p);
            String c3 = allParams.get("c3_" + p);
            String total = allParams.get("total_" + p);

            if (allEmpty(c1, c2, c3, total)) continue;

            Double d1 = parse(c1, "Component 1", sid, errors);
            Double d2 = parse(c2, "Component 2", sid, errors);
            Double d3 = parse(c3, "Component 3", sid, errors);
            Double tot = parse(total, "Total", sid, errors);

            if (d1 != null && !valid(d1)) errors.add("C1 of " + sid + " must be 0–10");
            if (d2 != null && !valid(d2)) errors.add("C2 of " + sid + " must be 0–10");
            if (d3 != null && !valid(d3)) errors.add("C3 of " + sid + " must be 0–10");
            if (tot != null && !valid(tot)) errors.add("Total of " + sid + " must be 0–10");

            if (!errors.isEmpty()) continue;

            String transcriptId = classId + "_" + sid;

            SpecializedAcademicTranscripts transcript = transcriptsService
                    .findOrCreateTranscript(transcriptId, s, clazz, staff);

            transcript.setScoreComponent1(d1);
            transcript.setScoreComponent2(d2);
            transcript.setScoreComponent3(d3);
            transcript.setScore(tot);

            transcriptsService.saveOrUpdateTranscript(transcript);
            saved++;
        }

        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Saved " + saved + ". Errors: " + String.join("; ", errors));
        } else if (saved > 0) {
            ra.addFlashAttribute("successMessage", "Saved scores for " + saved + " students.");
        } else {
            ra.addFlashAttribute("warningMessage", "No data saved.");
        }

        return "redirect:/staff-home/specialized-classes-list/enter-transcript";
    }

    private boolean allEmpty(String... v) {
        return Arrays.stream(v).allMatch(s -> s == null || s.trim().isEmpty());
    }

    private Double parse(String v, String field, String id, List<String> e) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return Double.parseDouble(v); }
        catch (NumberFormatException ex) { e.add(field + " of " + id + " invalid"); return null; }
    }

    private boolean valid(Double d) {
        return d != null && d >= 0 && d <= 10;
    }
}