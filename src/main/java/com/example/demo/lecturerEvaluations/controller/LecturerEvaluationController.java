// src/main/java/com/example/demo/lecturerEvaluations/controller/LecturerEvaluationController.java
package com.example.demo.lecturerEvaluations.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.lecturerEvaluations.model.LecturerEvaluations;
import com.example.demo.lecturerEvaluations.service.LecturerEvaluationService;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student-home/student-classes-list/evaluation")
public class LecturerEvaluationController {

    private final LecturerEvaluationService evaluationService;
    private final StudentsService studentsService;
    private final ClassesService classesService;
    private final MajorLecturers_MajorClassesService majorLecturersMajorClassesService;
    private final MinorLecturers_MinorClassesService minorLecturersMinorClassesService;
    private final MajorLecturers_SpecializedClassesService majorLecturersSpecializedClassesService;
    private final MajorLecturersService majorLecturersService;
    private final MinorLecturersService minorLecturersService;

    public LecturerEvaluationController(
            LecturerEvaluationService evaluationService,
            StudentsService studentsService,
            ClassesService classesService,
            MajorLecturers_MajorClassesService majorLecturersMajorClassesService,
            MinorLecturers_MinorClassesService minorLecturersMinorClassesService,
            MajorLecturers_SpecializedClassesService majorLecturersSpecializedClassesService,
            MajorLecturersService majorLecturersService,
            MinorLecturersService minorLecturersService) {

        this.evaluationService = evaluationService;
        this.studentsService = studentsService;
        this.classesService = classesService;
        this.majorLecturersMajorClassesService = majorLecturersMajorClassesService;
        this.minorLecturersMinorClassesService = minorLecturersMinorClassesService;
        this.majorLecturersSpecializedClassesService = majorLecturersSpecializedClassesService;
        this.majorLecturersService = majorLecturersService;
        this.minorLecturersService = minorLecturersService;
    }

    // 1. POST: Nhận classId từ danh sách lớp → lưu vào session → redirect GET
    @PostMapping
    public String enterEvaluationPage(@RequestParam String classId, HttpSession session) {
        session.setAttribute("evalClassId", classId);
        return "redirect:/student-home/student-classes-list/evaluation";
    }

    // 2. GET: Hiển thị trang đánh giá (lấy classId từ session) → URL sạch
    @GetMapping
    public String showEvaluations(HttpSession session, Model model, RedirectAttributes ra) {
        String classId = (String) session.getAttribute("evalClassId");

        if (classId == null) {
            ra.addFlashAttribute("error", "Please select a class to view evaluations.");
            return "redirect:/student-home/student-classes-list";
        }

        Students student = studentsService.getStudent();
        Classes classEntity = classesService.findClassById(classId);
        if (classEntity == null) {
            session.removeAttribute("evalClassId");
            ra.addFlashAttribute("error", "Class not found.");
            return "redirect:/student-home/student-classes-list";
        }

        List<Object> lecturersInClass = new ArrayList<>();
        if (classEntity instanceof MajorClasses majorClass) {
            lecturersInClass.addAll(majorLecturersMajorClassesService.listLecturersInClass(majorClass));
        }
        if (classEntity instanceof MinorClasses minorClass) {
            lecturersInClass.addAll(minorLecturersMinorClassesService.listLecturersInClass(minorClass));
        }
        if (classEntity instanceof SpecializedClasses specClass) {
            lecturersInClass.addAll(majorLecturersSpecializedClassesService.listLecturersInClass(specClass));
        }

        List<LecturerEvaluations> evaluations = evaluationService.findByClassIdByStudentId(classId, student.getId());

        model.addAttribute("classEntity", classEntity);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("evaluations", evaluations);
        model.addAttribute("student", student);
        model.addAttribute("now", LocalDateTime.now());

        return "LecturerEvaluationList";
    }

    // 3. POST: Submit đánh giá → lấy classId từ session
    @PostMapping("/submit")
    public String submitEvaluation(
            @RequestParam String lecturerId,
            @RequestParam String text,
            HttpSession session,
            RedirectAttributes ra) {

        String classId = (String) session.getAttribute("evalClassId");
        if (classId == null) {
            ra.addFlashAttribute("error", "Session expired. Please try again.");
            return "redirect:/student-home/student-classes-list";
        }

        Students currentStudent = studentsService.getStudent();

        try {
            Classes classEntity = classesService.findClassById(classId);

            if (classEntity instanceof MajorClasses || classEntity instanceof SpecializedClasses) {
                evaluationService.addMajorLecturerEvaluation(
                        currentStudent,
                        classEntity,
                        majorLecturersService.getLecturerById(lecturerId),
                        text
                );
            } else {
                evaluationService.addMinorLecturerEvaluation(
                        currentStudent,
                        classEntity,
                        minorLecturersService.getMinorLecturerById(lecturerId),
                        text
                );
            }

            ra.addFlashAttribute("message", "Thank you! Your review has been submitted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Failed to submit review. Please try again.");
        }

        return "redirect:/student-home/student-classes-list/evaluation"; // URL đẹp, không lộ ID
    }
    @PostMapping("/delete")
    public String deleteEvaluation(
            @RequestParam String evaluationId,
            @RequestParam String classId,
            RedirectAttributes ra) {  // hoặc dùng studentsService.getStudent()

        Students currentStudent = studentsService.getStudent();

        try {
            LecturerEvaluations eval = evaluationService.findById(evaluationId);
            if (eval == null) {
                ra.addFlashAttribute("error", "Review not found.");
            } else if (!eval.getReviewer().getId().equals(currentStudent.getId())) {
                ra.addFlashAttribute("error", "You can only delete your own reviews.");
            } else {
                evaluationService.deleteById(evaluationId);
                ra.addFlashAttribute("message", "Your review has been deleted.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete review.");
        }

        return "redirect:/student-home/student-classes-list/evaluation";
    }
}