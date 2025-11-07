package com.example.demo.students_Classes.students_SpecializedClasses.controller;

import com.example.demo.classes.abstractClasses.service.ClassesService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.students_Classes.students_SpecializedClasses.model.Students_SpecializedClasses;
import com.example.demo.students_Classes.students_SpecializedClasses.service.StudentsSpecializedClassesService;
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
@RequestMapping("/staff-home/specialized-classes-list")
@PreAuthorize("hasRole('STAFF')")
public class SpecializedClassesMemberArrangementController {

    private final StudentsSpecializedClassesService studentsSpecializedClassesService;
    private final SpecializedClassesService specializedClassesService;
    private final MajorLecturers_SpecializedClassesService lecturersClassesService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final ClassesService classesService;
    private final ReStudyPaymentService reStudyPaymentService;

    @Autowired
    public SpecializedClassesMemberArrangementController(
            StudentsSpecializedClassesService studentsSpecializedClassesService,
            SpecializedClassesService specializedClassesService,
            MajorLecturers_SpecializedClassesService lecturersClassesService,
            StaffsService staffsService,
            StudentsService studentsService,
            ClassesService classesService,
            ReStudyPaymentService reStudyPaymentService) {
        this.studentsSpecializedClassesService = studentsSpecializedClassesService;
        this.specializedClassesService = specializedClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.classesService = classesService;
        this.reStudyPaymentService = reStudyPaymentService;
    }

    @PostMapping("/member-arrangement")
    public String handleMemberArrangementForm(
            @RequestParam("classId") String classId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
        if (classEntity == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/staff-home/specialized-classes-list";
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/specialized-classes-list/member-arrangement";
    }

    @GetMapping("/member-arrangement")
    public String showMemberArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) {
            populateEmptyModel(model);
            model.addAttribute("errorMessage", "No class selected");
            return "SpecializedClassMemberArrangement";
        }

        SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
        if (classEntity == null) {
            populateEmptyModel(model);
            model.addAttribute("errorMessage", "Class not found");
            return "SpecializedClassMemberArrangement";
        }

        String subjectId = classEntity.getSpecializedSubject().getSubjectId();

        // Danh sách cũ
        List<Students_SpecializedClasses> studentsInClassRaw = studentsSpecializedClassesService.getStudentsInClass(classId);
        List<Students> studentsNotInClass = studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId);
        List<MajorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(classEntity);
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(classEntity);

        // === RETAKE ===
        List<RetakeSubjects> retakeList = reStudyPaymentService.getRetakeSubjectsBySubjectId(subjectId);
        List<Students> studentsFailedAndPaid = retakeList.stream()
                .map(RetakeSubjects::getStudent)
                .filter(Objects::nonNull)
                .toList();

        // Gửi dữ liệu
        model.addAttribute("class", classEntity);
        model.addAttribute("studentsInClass", studentsInClassRaw);
        model.addAttribute("studentsNotInClass", studentsNotInClass);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
        model.addAttribute("studentsFailedAndPaid", studentsFailedAndPaid);
        model.addAttribute("retakeList", retakeList);

        return "SpecializedClassMemberArrangement";
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
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            if (classEntity == null) {
                errors.add("Class not found");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for removal");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            for (String studentId : studentIds) {
                if (studentsSpecializedClassesService.isStudentAlreadyRequiredForClass(studentId, classId)) {
                    studentsSpecializedClassesService.removeStudentFromClass(studentId, classId);
                } else {
                    errors.add("Student with ID " + studentId + " is not in this class");
                }
            }

            if (!errors.isEmpty()) {
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Selected students removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            populateErrorModel(model, specializedClassesService.getClassById(classId), errors);
            return "SpecializedClassMemberArrangement";
        }
    }

    @PostMapping("/add-student-to-class")
    public String addStudentToClass(
            @RequestParam("classId") String classId,
            @RequestParam(value = "studentIds", required = false) List<String> studentIds,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        List<String> errors = new ArrayList<>();

        try {
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            if (classEntity == null) {
                errors.add("Class not found");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            Staffs currentStaff = staffsService.getStaff();
            if (currentStaff == null) {
                errors.add("Staff information not found");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for assignment");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            int addedCount = 0;
            for (String studentId : studentIds) {
                if (!studentsSpecializedClassesService.isStudentAlreadyRequiredForClass(studentId, classId)) {
                    Students student = studentsService.getStudentById(studentId);
                    if (student != null) {
                        Students_SpecializedClasses ssc = new Students_SpecializedClasses();
                        StudentsClassesId id = new StudentsClassesId();
                        id.setStudentId(studentId);
                        id.setClassId(classId);
                        ssc.setId(id);
                        ssc.setStudent(student);
                        ssc.setSpecializedClass(classEntity);
                        ssc.setClassEntity(classesService.findClassById(classId));
                        ssc.setAddedBy(currentStaff);
                        ssc.setCreatedAt(LocalDateTime.now());
                        studentsSpecializedClassesService.addStudentToClass(ssc);
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
                return "SpecializedClassMemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", addedCount + " student(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            populateErrorModel(model, specializedClassesService.getClassById(classId), errors);
            return "SpecializedClassMemberArrangement";
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
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            if (classEntity == null) {
                errors.add("Class not found");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for removal");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            lecturersClassesService.removeLecturerFromClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            populateErrorModel(model, specializedClassesService.getClassById(classId), errors);
            return "SpecializedClassMemberArrangement";
        }
    }

    @PostMapping("/add-lecturer-to-class")
    public String addLecturerToClass(
            @RequestParam("classId") String classId,
            @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        List<String> errors = new ArrayList<>();

        try {
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            if (classEntity == null) {
                errors.add("Class not found");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for assignment");
                populateErrorModel(model, classEntity, errors);
                return "SpecializedClassMemberArrangement";
            }

            lecturersClassesService.addLecturersToClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred: " + e.getMessage());
            populateErrorModel(model, specializedClassesService.getClassById(classId), errors);
            return "SpecializedClassMemberArrangement";
        }
    }

    // === HỖ TRỢ ===
    private void populateEmptyModel(Model model) {
        model.addAttribute("class", new SpecializedClasses());
        model.addAttribute("studentsInClass", new ArrayList<Students_SpecializedClasses>());
        model.addAttribute("studentsNotInClass", new ArrayList<Students>());
        model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
        model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
        model.addAttribute("studentsFailedAndPaid", new ArrayList<Students>());
        model.addAttribute("retakeList", new ArrayList<RetakeSubjects>());
    }

    private void populateErrorModel(Model model, SpecializedClasses classEntity, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", classEntity != null ? classEntity : new SpecializedClasses());
        String classId = classEntity != null ? classEntity.getClassId() : null;
        String subjectId = classEntity != null ? classEntity.getSpecializedSubject().getSubjectId() : "";
        model.addAttribute("studentsInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsInClass(classId) : new ArrayList<>());
        model.addAttribute("studentsNotInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId) : new ArrayList<>());
        model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<>());
        model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<>());
        model.addAttribute("studentsFailedAndPaid", new ArrayList<Students>());
        model.addAttribute("retakeList", new ArrayList<RetakeSubjects>());
    }
}