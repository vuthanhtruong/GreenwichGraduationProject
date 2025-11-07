package com.example.demo.students_Classes.students_MajorClass.controller;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.students_Classes.students_MajorClass.model.Students_MajorClasses;
import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
import com.example.demo.RetakeSubjects.model.RetakeSubjects;
import com.example.demo.RetakeSubjects.service.ReStudyPaymentService;
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
import java.util.Objects;

@Controller
@RequestMapping("/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class MemberArrangementController {

    private final StudentsMajorClassesService studentsMajorClassesService;
    private final MajorClassesService classesService;
    private final MajorLecturers_MajorClassesService lecturersClassesService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final ReStudyPaymentService reStudyPaymentService; // ← MỚI

    @Autowired
    public MemberArrangementController(
            StudentsMajorClassesService studentsMajorClassesService,
            MajorClassesService classesService,
            MajorLecturers_MajorClassesService lecturersClassesService,
            StaffsService staffsService,
            StudentsService studentsService,
            ReStudyPaymentService reStudyPaymentService) { // ← THÊM SERVICE
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.classesService = classesService;
        this.lecturersClassesService = lecturersClassesService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.reStudyPaymentService = reStudyPaymentService;
    }

    @GetMapping("/member-arrangement")
    public String showMemberArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) {
            model.addAttribute("errorMessage", "No class selected");
            model.addAttribute("class", new MajorClasses());
            model.addAttribute("studentsInClass", new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", new ArrayList<Students>());
            model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
            model.addAttribute("studentsFailedAndPaid", new ArrayList<Students>());
            model.addAttribute("retakeList", new ArrayList<RetakeSubjects>());
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
            model.addAttribute("studentsFailedAndPaid", new ArrayList<Students>());
            model.addAttribute("retakeList", new ArrayList<RetakeSubjects>());
            return "MemberArrangement";
        }

        // === DANH SÁCH CŨ ===
        List<Students> studentsInClass = studentsMajorClassesService.getStudentsByClass(classEntity);
        List<Students> studentsNotInClass = studentsMajorClassesService.getStudentsNotInClassAndSubject(
                classId, classEntity.getSubject().getSubjectId());
        List<MajorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(classEntity);
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(classEntity);

        // === MỚI: SINH VIÊN ĐÃ ĐĂNG KÝ HỌC LẠI (RETAKE) ===
        String subjectId = classEntity.getSubject().getSubjectId();
        List<RetakeSubjects> retakeList = reStudyPaymentService.getRetakeSubjectsBySubjectId(subjectId);

        List<Students> studentsFailedAndPaid = retakeList.stream()
                .map(RetakeSubjects::getStudent)
                .filter(Objects::nonNull)
                .toList();

        // === GỬI DỮ LIỆU ===
        model.addAttribute("class", classEntity);
        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("studentsNotInClass", studentsNotInClass);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsFailedAndPaid", studentsFailedAndPaid);
        model.addAttribute("retakeList", retakeList); // ← CẦN ĐỂ HIỂN THỊ createdAt

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
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for removal");
                populateErrorModel(model, classEntity, errors);
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
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Selected students removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            populateErrorModel(model, classEntity, errors);
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
            if (classEntity == null) {
                errors.add("Class not found");
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            Staffs currentStaff = staffsService.getStaff();
            if (currentStaff == null) {
                errors.add("Staff information not found");
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for assignment");
                populateErrorModel(model, classEntity, errors);
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
                        smc.setClassEntity(classEntity);
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
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", addedCount + " student(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            populateErrorModel(model, classEntity, errors);
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
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            if (classEntity == null) {
                errors.add("Class not found");
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for assignment");
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            lecturersClassesService.addLecturersToClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            populateErrorModel(model, classEntity, errors);
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
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for removal");
                populateErrorModel(model, classEntity, errors);
                return "MemberArrangement";
            }

            lecturersClassesService.removeLecturerFromClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            MajorClasses classEntity = (MajorClasses) classesService.getClassById(classId);
            populateErrorModel(model, classEntity, errors);
            return "MemberArrangement";
        }
    }

    // === HÀM HỖ TRỢ ===
    private void populateErrorModel(Model model, MajorClasses classEntity, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", classEntity != null ? classEntity : new MajorClasses());
        model.addAttribute("studentsInClass", classEntity != null ? studentsMajorClassesService.getStudentsByClass(classEntity) : new ArrayList<>());
        model.addAttribute("studentsNotInClass", classEntity != null ? studentsMajorClassesService.getStudentsNotInClassAndSubject(
                classEntity.getClassId(), classEntity.getSubject().getSubjectId()) : new ArrayList<>());
        model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<>());
        model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<>());
        model.addAttribute("studentsFailedAndPaid", new ArrayList<Students>());
        model.addAttribute("retakeList", new ArrayList<RetakeSubjects>());
    }
}