package com.example.demo.academicTranscript.controller;

import com.example.demo.academicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.academicTranscript.service.AcademicTranscriptsService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
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
@RequestMapping("/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class TranscriptEntryController {

    private final MajorClassesService majorClassesService;
    private final StudentsMajorClassesService studentsMajorClassesService;
    private final AcademicTranscriptsService academicTranscriptsService;
    private final StaffsService staffsService;

    public TranscriptEntryController(MajorClassesService majorClassesService,
                                     StudentsMajorClassesService studentsMajorClassesService,
                                     AcademicTranscriptsService academicTranscriptsService,
                                     StaffsService staffsService) {
        this.majorClassesService = majorClassesService;
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.academicTranscriptsService = academicTranscriptsService;
        this.staffsService = staffsService;
    }

    // === 1. POST: Nhận classId từ form ẩn (không lộ URL) ===
    @PostMapping("/enter-transcript")
    public String loadTranscriptPage(@RequestParam("classId") String classId,
                                     Model model,
                                     RedirectAttributes ra,
                                     HttpSession session) {

        return loadTranscript(classId, model, ra, session);
    }

    // === 2. GET: Dùng khi quay lại trang (có classId trong session) ===
    @GetMapping("/enter-transcript")
    public String showTranscriptPage(@RequestParam(value = "classId", required = false) String classId,
                                     Model model,
                                     HttpSession session,
                                     RedirectAttributes ra) {

        // Nếu không có classId → lấy từ session
        if (classId == null || classId.isBlank()) {
            classId = (String) session.getAttribute("currentClassId");
        }

        if (classId == null || classId.isBlank()) {
            ra.addFlashAttribute("errorMessage", "Class ID is required.");
            return "redirect:/staff-home/classes-list";
        }

        return loadTranscript(classId, model, ra, session);
    }

    // === Helper: Tái sử dụng logic hiển thị trang ===
    private String loadTranscript(String classId, Model model, RedirectAttributes ra, HttpSession session) {
        MajorClasses clazz = majorClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found!");
            return "redirect:/staff-home/classes-list";
        }

        List<Students> students = studentsMajorClassesService.getStudentsByClass(clazz);
        if (students.isEmpty()) {
            model.addAttribute("warningMessage", "No students enrolled in this class.");
        }

        List<MajorAcademicTranscripts> transcripts = academicTranscriptsService.getTranscriptsByClass(clazz);

        // Tạo map: studentId → transcript
        Map<String, MajorAcademicTranscripts> transcriptMap = transcripts.stream()
                .collect(Collectors.toMap(
                        t -> t.getStudent().getId(),
                        t -> t,
                        (existing, replacement) -> existing
                ));

        model.addAttribute("clazz", clazz);
        model.addAttribute("students", students);
        model.addAttribute("transcriptMap", transcriptMap);

        session.setAttribute("currentClassId", classId);
        return "EnterTranscript";
    }

    // === 3. POST: Lưu điểm (giữ nguyên) ===
    @PostMapping("/save-transcript")
    @Transactional
    public String saveTranscript(@RequestParam("classId") String classId,
                                 @RequestParam Map<String, String> allParams,
                                 RedirectAttributes ra) {

        MajorClasses clazz = majorClassesService.getClassById(classId);
        if (clazz == null) {
            ra.addFlashAttribute("errorMessage", "Class not found!");
            return "redirect:/staff-home/classes-list";
        }

        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            ra.addFlashAttribute("errorMessage", "Staff information not found!");
            return "redirect:/staff-home/classes-list";
        }

        List<Students> students = studentsMajorClassesService.getStudentsByClass(clazz);
        if (students.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "No students to grade.");
            return "redirect:/staff-home/classes-list/enter-transcript";
        }

        List<String> errors = new ArrayList<>();
        int saved = 0;

        for (Students student : students) {
            String sid = student.getId();
            String prefix = sid + "_";

            String c1 = allParams.get("c1_" + prefix);
            String c2 = allParams.get("c2_" + prefix);
            String c3 = allParams.get("c3_" + prefix);
            String total = allParams.get("total_" + prefix);

            if (isEmpty(c1) && isEmpty(c2) && isEmpty(c3) && isEmpty(total)) {
                continue;
            }

            Double d1 = parseDouble(c1, "Component 1", sid, errors);
            Double d2 = parseDouble(c2, "Component 2", sid, errors);
            Double d3 = parseDouble(c3, "Component 3", sid, errors);
            Double tot = parseDouble(total, "Total", sid, errors);

            if (d1 != null && !valid(d1)) errors.add("Component 1 of " + sid + " must be 0–10");
            if (d2 != null && !valid(d2)) errors.add("Component 2 of " + sid + " must be 0–10");
            if (d3 != null && !valid(d3)) errors.add("Component 3 of " + sid + " must be 0–10");
            if (tot != null && !valid(tot)) errors.add("Total of " + sid + " must be 0–10");

            if (!errors.isEmpty()) continue;

            String transcriptId = classId + "_" + sid;

            MajorAcademicTranscripts transcript = academicTranscriptsService
                    .findOrCreateTranscript(transcriptId, student, clazz, staff);

            transcript.setScoreComponent1(d1);
            transcript.setScoreComponent2(d2);
            transcript.setScoreComponent3(d3);
            transcript.setScore(tot);

            academicTranscriptsService.saveOrUpdateTranscript(transcript);
            saved++;
        }

        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errorMessage",
                    "Saved " + saved + " records. Errors: " + String.join("; ", errors));
        } else if (saved > 0) {
            ra.addFlashAttribute("successMessage",
                    "Successfully saved scores for " + saved + " students.");
        } else {
            ra.addFlashAttribute("warningMessage", "No data was saved.");
        }

        return "redirect:/staff-home/classes-list/enter-transcript";
    }

    // === Helper methods ===
    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
    private Double parseDouble(String val, String field, String sid, List<String> err) {
        if (isEmpty(val)) return null;
        try { return Double.parseDouble(val); }
        catch (NumberFormatException e) { err.add(field + " of " + sid + " is not a valid number"); return null; }
    }
    private boolean valid(Double d) { return d != null && d >= 0 && d <= 10; }
}