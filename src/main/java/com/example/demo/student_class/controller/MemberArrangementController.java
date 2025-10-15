package com.example.demo.student_class.controller;

import com.example.demo.classes.model.Classes;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.classes.service.MajorClassesService;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer_class.service.Lecturers_ClassesService;
import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import com.example.demo.student_class.model.StudentsClassesId;
import com.example.demo.student_class.model.Students_MajorClasses;
import com.example.demo.student_class.service.StudentsMajorClassesService;
import com.example.demo.staff.model.Staffs;
import com.example.demo.staff.service.StaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class MemberArrangementController {

    private final StudentsMajorClassesService studentsMajorClassesService;
    private final MajorClassesService classesService;
    private final Lecturers_ClassesService lecturersClassesService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;

    @Autowired
    public MemberArrangementController(
            StudentsMajorClassesService studentsMajorClassesService,
            MajorClassesService classesService,
            Lecturers_ClassesService lecturersClassesService,
            StaffsService staffsService,
            StudentsService studentsService) {
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.classesService = classesService;
        this.lecturersClassesService = lecturersClassesService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
    }

    @GetMapping("/member-arrangement")
    public String showMemberArrangement(
            Model model,
            HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) {
            model.addAttribute("errorMessage", "No class selected");
            model.addAttribute("class", new MajorClasses());
            model.addAttribute("studentsInClass", new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", new ArrayList<Students>());
            model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
            return "MemberArrangement";
        }

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
        return "redirect:/staff-home/classes-list/member-arrangement";
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
                model.addAttribute("studentsNotInClass", studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId()));
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
                model.addAttribute("studentsNotInClass", studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId()));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Selected students removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while removing students: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new MajorClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsMajorClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsMajorClassesService.getStudentsNotInClassAndSubject(
                    classId, classEntity.getSubject().getSubjectId()) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
            return "MemberArrangement";
        }
    }

    @PostMapping("/add-students-to-class")
    public String addStudentToClass(
            @RequestParam("classId") String classId,
            @RequestParam(value = "studentIds", required = false) List<String> studentIds,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        List<String> errors = new ArrayList<>();

        try {
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            Classes currentClass = (Classes) classesService.getClassById(classId);
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

            Staffs currentStaff = staffsService.getStaff();
            if (currentStaff == null) {
                errors.add("Staff information not found");
                model.addAttribute("errorMessage", "Staff information not found");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(classEntity));
                model.addAttribute("studentsNotInClass", studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId()));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for assignment");
                model.addAttribute("errorMessage", "No students selected for assignment");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(classEntity));
                model.addAttribute("studentsNotInClass", studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId()));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            int addedCount = 0;
            for (String studentId : studentIds) {
                if (!studentsMajorClassesService.existsByStudentAndClass(studentId, classId)) {
                    Students student = studentsService.getStudentById(studentId);
                    if (student != null) {
                        StudentsClassesId sId = new StudentsClassesId();
                        sId.setClassId(classId);
                        sId.setStudentId(studentId);
                        Students_MajorClasses smc = new Students_MajorClasses();
                        smc.setId(sId);
                        smc.setStudent(student);
                        smc.setMajorClass(classEntity);
                        smc.setClassEntity(currentClass);
                        smc.setAddedBy(currentStaff);
                        smc.setCreatedAt(LocalDateTime.now());
                        studentsMajorClassesService.addStudentToClass(smc);
                        addedCount++;
                    } else {
                        errors.add("Student with ID " + studentId + " not found");
                    }
                } else {
                    errors.add("Student with ID " + studentId + " is already in this class");
                }
            }

            if (!errors.isEmpty()) {
                model.addAttribute("errorMessage", String.join("; ", errors));
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(classEntity));
                model.addAttribute("studentsNotInClass", studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId()));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", addedCount + " student(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while adding students: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new MajorClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsMajorClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsMajorClassesService.getStudentsNotInClassAndSubject(
                    classId, classEntity.getSubject().getSubjectId()) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
            return "MemberArrangement";
        }
    }

    @PostMapping("/add-lecturers-to-class")
    public String addLecturerToClass(
            @RequestParam("classId") String classId,
            @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        List<String> errors = new ArrayList<>();

        try {
            MajorClasses classEntity = classesService.getClassById(classId);
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

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for assignment");
                model.addAttribute("errorMessage", "No lecturers selected for assignment");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(classEntity));
                model.addAttribute("studentsNotInClass", studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId()));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            lecturersClassesService.addLecturersToClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while adding lecturers: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new MajorClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsMajorClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsMajorClassesService.getStudentsNotInClassAndSubject(
                    classId, classEntity.getSubject().getSubjectId()) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
            return "MemberArrangement";
        }
    }

    @PostMapping("/remove-lecturer-from-class")
    public String removeLecturerFromClass(
            @RequestParam("classId") String classId,
            @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
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

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for removal");
                model.addAttribute("errorMessage", "No lecturers selected for removal");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(classEntity));
                model.addAttribute("studentsNotInClass", studentsMajorClassesService.getStudentsNotInClassAndSubject(
                        classId, classEntity.getSubject().getSubjectId()));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "MemberArrangement";
            }

            lecturersClassesService.removeLecturerFromClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while removing lecturers: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new MajorClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsMajorClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsMajorClassesService.getStudentsNotInClassAndSubject(
                    classId, classEntity.getSubject().getSubjectId()) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
            return "MemberArrangement";
        }
    }
}