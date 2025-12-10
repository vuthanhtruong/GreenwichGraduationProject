package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.retakeSubjects.service.RetakeSubjectsService;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
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
@RequestMapping("/deputy-staff-home/minor-classes-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorTranscriptEntryController {

    private final MinorClassesService minorClassesService;
    private final StudentsMinorClassesService studentsMinorClassesService;
    private final AcademicTranscriptsService transcriptsService;
    private final DeputyStaffsService deputyStaffsService;
    private final RetakeSubjectsService retakeSubjectsService;

    public MinorTranscriptEntryController(
            MinorClassesService minorClassesService,
            StudentsMinorClassesService studentsMinorClassesService,
            AcademicTranscriptsService transcriptsService,
            DeputyStaffsService deputyStaffsService, RetakeSubjectsService retakeSubjectsService) {
        this.minorClassesService = minorClassesService;
        this.studentsMinorClassesService = studentsMinorClassesService;
        this.transcriptsService = transcriptsService;
        this.deputyStaffsService = deputyStaffsService;
        this.retakeSubjectsService = retakeSubjectsService;
    }

    // === LOAD TRANG ===
    @GetMapping("/enter-transcript")
    public String show(@RequestParam(value = "classId", required = false) String classId,
                       Model model, HttpSession session, RedirectAttributes ra) {
        if (classId == null || classId.isBlank()) {
            classId = (String) session.getAttribute("currentMinorClassId");
        }
        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("errorMessage", "Class ID is required.");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }
        return load(classId, model, ra, session);
    }

    @PostMapping("/enter-transcript")
    public String loadPage(@RequestParam("classId") String classId,
                           Model model, RedirectAttributes ra, HttpSession session) {
        return load(classId, model, ra, session);
    }

    private String load(String classId, Model model, RedirectAttributes ra, HttpSession session) {
        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found!");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        List<Students> allStudents = studentsMinorClassesService.getStudentsByClass(clazz);
        List<MinorAcademicTranscripts> transcripts = transcriptsService.getTranscriptsByClass(clazz);

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

        session.setAttribute("currentMinorClassId", classId);
        return "MinorEnterTranscript";
    }

    // === LƯU ĐIỂM ===
    @PostMapping("/save-transcript")
    @Transactional
    public String save(@RequestParam("classId") String classId,
                       @RequestParam Map<String, String> allParams,
                       RedirectAttributes ra) {

        MinorClasses clazz = minorClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found!");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        DeputyStaffs deputy = deputyStaffsService.getDeputyStaff();
        if (deputy == null) {
            ra.addFlashAttribute("errorMessage", "Deputy Staff not found!");
            return "redirect:/deputy-staff-home/minor-classes-list";
        }

        List<Students> allStudents = studentsMinorClassesService.getStudentsByClass(clazz);
        if (allStudents.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "No students to grade.");
            return "redirect:/deputy-staff-home/minor-classes-list/enter-transcript?classId=" + classId;
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

            MinorAcademicTranscripts transcript = transcriptsService
                    .findOrCreateTranscript(transcriptId, student, clazz, deputy);

            transcript.setScoreComponent1(c1);
            transcript.setScoreComponent2(c2);
            transcript.setScoreComponent3(c3);
            transcript.setGrade(grade);
            if(grade.equals(Grades.REFER)){
                retakeSubjectsService.deleteByStudentAndSubject(student.getId(), clazz.getMinorSubject().getSubjectId());
            }

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

        return "redirect:/deputy-staff-home/minor-classes-list/enter-transcript?classId=" + classId;
    }

    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
    private Double parseDouble(String val, String field, String sid, List<String> err) {
        if (isEmpty(val)) return null;
        try { return Double.parseDouble(val); }
        catch (NumberFormatException e) { err.add(field + " of " + sid + " is not a number"); return null; }
    }
    private boolean valid(Double d) { return d != null && d >= 0 && d <= 10; }
}