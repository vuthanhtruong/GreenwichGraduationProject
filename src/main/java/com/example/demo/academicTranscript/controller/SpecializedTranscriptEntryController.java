package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.model.SpecializedAcademicTranscripts;
import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.entity.Enums.Grades;
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

    @GetMapping("/enter-transcript")
    public String show(@RequestParam(value = "classId", required = false) String classId,
                       Model model, HttpSession session, RedirectAttributes ra) {
        if (classId == null || classId.isBlank()) {
            classId = (String) session.getAttribute("currentSpecializedClassId");
        }
        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("errorMessage", "Class ID is required.");
            return "redirect:/staff-home/specialized-classes-list";
        }
        return load(classId, model, ra, session);
    }

    @PostMapping("/enter-transcript")
    public String loadPage(@RequestParam("classId") String classId,
                           Model model, RedirectAttributes ra, HttpSession session) {
        return load(classId, model, ra, session);
    }

    private String load(String classId, Model model, RedirectAttributes ra, HttpSession session) {
        SpecializedClasses clazz = specializedClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found!");
            return "redirect:/staff-home/specialized-classes-list";
        }

        List<Students> allStudents = studentsSpecializedService.getStudentsByClass(clazz);
        List<SpecializedAcademicTranscripts> transcripts = transcriptsService.getTranscriptsByClass(clazz);

        Set<String> studentIdsWithScores = transcripts.stream()
                .map(t -> t.getStudent().getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Students> studentsWithScores = new ArrayList<>();
        List<Students> studentsWithoutScores = new ArrayList<>();

        for (Students s : allStudents) {
            if (studentIdsWithScores.contains(s.getId())) {
                studentsWithScores.add(s);
            } else {
                studentsWithoutScores.add(s);
            }
        }

        // THÊM ENUM GRADES VÀO MODEL
        model.addAttribute("grades", Grades.values());

        model.addAttribute("clazz", clazz);
        model.addAttribute("transcripts", transcripts);
        model.addAttribute("studentsWithScores", studentsWithScores);
        model.addAttribute("studentsWithoutScores", studentsWithoutScores);

        session.setAttribute("currentSpecializedClassId", classId);
        return "SpecializedEnterTranscript";
    }

    @PostMapping("/save-transcript")
    @Transactional
    public String save(@RequestParam("classId") String classId,
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

        List<Students> allStudents = studentsSpecializedService.getStudentsByClass(clazz);
        if (allStudents.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "No students to grade.");
            return "redirect:/staff-home/specialized-classes-list/enter-transcript?classId=" + classId;
        }

        Map<String, Students> studentMap = new HashMap<>();
        for (Students s : allStudents) {
            studentMap.put(s.getId(), s);
        }

        List<String> errors = new ArrayList<>();
        int saved = 0;

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith("c1_") && !key.startsWith("c2_") && !key.startsWith("c3_") && !key.startsWith("grade_")) {
                continue;
            }

            String sid = key.substring(key.indexOf("_") + 1);
            if (sid.isBlank() || !studentMap.containsKey(sid)) continue;

            Students student = studentMap.get(sid);

            String c1Str = allParams.get("c1_" + sid);
            String c2Str = allParams.get("c2_" + sid);
            String c3Str = allParams.get("c3_" + sid);
            String gradeStr = allParams.get("grade_" + sid);

            if (isEmpty(c1Str) && isEmpty(c2Str) && isEmpty(c3Str) && isEmpty(gradeStr)) {
                continue;
            }

            Double c1 = parseDouble(c1Str, "C1", sid, errors);
            Double c2 = parseDouble(c2Str, "C2", sid, errors);
            Double c3 = parseDouble(c3Str, "C3", sid, errors);

            if (c1 != null && !valid(c1)) errors.add("C1 of " + sid + " must be 0–10");
            if (c2 != null && !valid(c2)) errors.add("C2 of " + sid + " must be 0–10");
            if (c3 != null && !valid(c3)) errors.add("C3 of " + sid + " must be 0–10");

            Grades grade = null;
            if (!isEmpty(gradeStr)) {
                try {
                    grade = Grades.valueOf(gradeStr);
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid grade for " + sid);
                }
            } else if (c1 != null || c2 != null || c3 != null) {
                errors.add("Grade is required for " + sid);
            }

            if (!errors.isEmpty()) continue;

            String transcriptId = classId + "_" + sid;

            SpecializedAcademicTranscripts transcript = transcriptsService
                    .findOrCreateTranscript(transcriptId, student, clazz, staff);

            transcript.setScoreComponent1(c1);
            transcript.setScoreComponent2(c2);
            transcript.setScoreComponent3(c3);
            transcript.setGrade(grade);

            transcriptsService.saveOrUpdateTranscript(transcript);
            saved++;
        }

        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Saved " + saved + " records. Errors: " + String.join("; ", errors));
        } else if (saved > 0) {
            ra.addFlashAttribute("successMessage", "Saved scores for " + saved + " students.");
        } else {
            ra.addFlashAttribute("warningMessage", "No data was saved.");
        }

        return "redirect:/staff-home/specialized-classes-list/enter-transcript?classId=" + classId;
    }

    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
    private Double parseDouble(String val, String field, String sid, List<String> err) {
        if (isEmpty(val)) return null;
        try { return Double.parseDouble(val); }
        catch (NumberFormatException e) { err.add(field + " of " + sid + " is not a number"); return null; }
    }
    private boolean valid(Double d) { return d != null && d >= 0 && d <= 10; }
}