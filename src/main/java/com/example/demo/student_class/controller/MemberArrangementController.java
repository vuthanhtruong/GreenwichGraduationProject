package com.example.demo.student_class.controller;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.ClassesService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer_class.service.Lecturers_ClassesService;
import com.example.demo.student.model.Students;
import com.example.demo.student_class.service.StudentsMajorClassesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
public class MemberArrangementController {

    private final StudentsMajorClassesService studentsMajorClassesService;
    private final ClassesService classesService;
    private final Lecturers_ClassesService lecturersClassesService;

    @Autowired
    public MemberArrangementController(
            StudentsMajorClassesService studentsMajorClassesService,
            ClassesService classesService,
            Lecturers_ClassesService lecturersClassesService) {
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.classesService = classesService;
        this.lecturersClassesService = lecturersClassesService;
    }

    @GetMapping("/member-arrangement")
    public String showMemberArrangement(
            @RequestParam("classId") String classId,
            Model model,
            HttpSession session) {
        MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
        if (classEntity == null) {
            model.addAttribute("errorMessage", "Class not found");
            model.addAttribute("class", new MajorClasses());
            model.addAttribute("studentsInClass", new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", new ArrayList<Students>());
            model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
            return "MemberArrangement";
        }

        List<Students> studentsInClass = studentsMajorClassesService.getStudentsByClass(classEntity);
        List<Students> studentsNotInClass = studentsMajorClassesService.getStudentsNotInClassAndSubject(
                classId, classEntity.getSubject().getSubjectId());
        List<MajorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(classEntity);
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(classEntity);

        model.addAttribute("class", classEntity);
        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("studentsNotInClass", studentsNotInClass);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        session.setAttribute("currentClassId", classId);
        return "MemberArrangement";
    }

    @PostMapping("/member-arrangement")
    public String handleMemberArrangementForm(
            @RequestParam("classId") String classId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
        if (classEntity == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/staff-home/classes-list";
        }

        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement?classId=" + classId;
    }

    @PostMapping("/remove-student-from-class")
    public String removeStudentFromClass(
            @RequestParam("classId") String classId,
            @RequestParam(value = "studentIds", required = false) List<String> studentIds,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        List<String> errors = new ArrayList<>();

        try {
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            if (classEntity == null) {
                errors.add("Class not found");
                model.addAttribute("errorMessage", "Class not found");
                model.addAttribute("class", new MajorClasses());
                model.addAttribute("studentsInClass", new ArrayList<Students>());
                model.addAttribute("studentsNotInClass", new ArrayList<Students>());
                model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
                model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
                return "MemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for removal");
                model.addAttribute("errorMessage", "No students selected for removal");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(classEntity));
                List<Students> studentsNotInClass = studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId());
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            for (String studentId : studentIds) {
                if (studentsMajorClassesService.existsByStudentAndClass(studentId, classId)) {
                    studentsMajorClassesService.removeStudentFromClass(studentId, classId);
                } else {
                    errors.add("Student with ID " + studentId + " is not in this class");
                }
            }

            if (!errors.isEmpty()) {
                model.addAttribute("errorMessage", String.join("; ", errors));
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(classEntity));
                List<Students> studentsNotInClass = studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId());
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Selected students removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement?classId=" + classId;
        } catch (Exception e) {
            errors.add("An error occurred while removing students: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classesService.getClassById(classId) != null ? classesService.getClassById(classId) : new MajorClasses());
            model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass((MajorClasses) classesService.getClassById(classId)));
            List<Students> studentsNotInClass = studentsMajorClassesService.getStudentsNotInClassAndSubject(
                    classId, classEntity.getSubject().getSubjectId());
            model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
            model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
            return "MemberArrangement";
        }
    }
}