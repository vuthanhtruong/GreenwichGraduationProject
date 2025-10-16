package com.example.demo.student_class.controller;

import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer_class.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.specializedClasses.service.SpecializedClassesService;
import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import com.example.demo.student_class.model.Students_SpecializedClasses;
import com.example.demo.student_class.service.StudentsSpecializedClassesService;
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
@RequestMapping("/staff-home/specialized-classes-list")
@PreAuthorize("hasRole('STAFF')")
public class SpecializedClassesMemberArrangementController {

    private final StudentsSpecializedClassesService studentsSpecializedClassesService;
    private final SpecializedClassesService specializedClassesService;
    private final MajorLecturers_SpecializedClassesService lecturersClassesService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;

    @Autowired
    public SpecializedClassesMemberArrangementController(
            StudentsSpecializedClassesService studentsSpecializedClassesService,
            SpecializedClassesService specializedClassesService,
            MajorLecturers_SpecializedClassesService lecturersClassesService,
            StaffsService staffsService,
            StudentsService studentsService) {
        this.studentsSpecializedClassesService = studentsSpecializedClassesService;
        this.specializedClassesService = specializedClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
    }

    @PostMapping("/member-arrangement")
    public String handleMemberArrangementForm(
            @RequestParam("id") String classId,
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
    public String showMemberArrangement(
            Model model,
            HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) {
            model.addAttribute("errorMessage", "No class selected");
            model.addAttribute("class", new SpecializedClasses());
            model.addAttribute("studentsInClass", new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", new ArrayList<Students>());
            model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
            return "SpecializedClassMemberArrangement";
        }

        SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
        if (classEntity == null) {
            model.addAttribute("errorMessage", "Class not found");
            model.addAttribute("class", new SpecializedClasses());
            model.addAttribute("studentsInClass", new ArrayList<Students>());
            model.addAttribute("studentsNotInClass", new ArrayList<Students>());
            model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
            return "SpecializedClassMemberArrangement";
        }

        String subjectId = classEntity.getSpecializedSubject().getSubjectId();
        List<Students> studentsInClass = studentsSpecializedClassesService.getStudentsByClass(classEntity);
        List<Students> studentsNotInClass = studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId);
        List<MajorLecturers> lecturersInClass = lecturersClassesService.listLecturersInClass(classEntity);
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(classEntity);

        model.addAttribute("class", classEntity);
        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("studentsNotInClass", studentsNotInClass);
        model.addAttribute("lecturersInClass", lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass);
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
                model.addAttribute("errorMessage", "Class not found");
                model.addAttribute("class", new SpecializedClasses());
                model.addAttribute("studentsInClass", new ArrayList<Students>());
                model.addAttribute("studentsNotInClass", new ArrayList<Students>());
                model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
                model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
                return "SpecializedClassMemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for removal");
                model.addAttribute("errorMessage", "No students selected for removal");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsSpecializedClassesService.getStudentsByClass(classEntity));
                String subjectId = classEntity.getSpecializedSubject().getSubjectId();
                model.addAttribute("studentsNotInClass", studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
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
                model.addAttribute("errorMessage", String.join("; ", errors));
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsSpecializedClassesService.getStudentsByClass(classEntity));
                String subjectId = classEntity.getSpecializedSubject().getSubjectId();
                model.addAttribute("studentsNotInClass", studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "SpecializedClassMemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Selected students removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while removing students: " + e.getMessage());
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new SpecializedClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            String subjectId = classEntity != null ? classEntity.getSpecializedSubject().getSubjectId() : "";
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
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
                model.addAttribute("errorMessage", "Class not found");
                model.addAttribute("class", new SpecializedClasses());
                model.addAttribute("studentsInClass", new ArrayList<Students>());
                model.addAttribute("studentsNotInClass", new ArrayList<Students>());
                model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
                model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
                return "SpecializedClassMemberArrangement";
            }

            Staffs currentStaff = staffsService.getStaff();
            if (currentStaff == null) {
                errors.add("Staff information not found");
                model.addAttribute("errorMessage", "Staff information not found");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsSpecializedClassesService.getStudentsByClass(classEntity));
                String subjectId = classEntity.getSpecializedSubject().getSubjectId();
                model.addAttribute("studentsNotInClass", studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "SpecializedClassMemberArrangement";
            }

            if (studentIds == null || studentIds.isEmpty()) {
                errors.add("No students selected for assignment");
                model.addAttribute("errorMessage", "No students selected for assignment");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsSpecializedClassesService.getStudentsByClass(classEntity));
                String subjectId = classEntity.getSpecializedSubject().getSubjectId();
                model.addAttribute("studentsNotInClass", studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "SpecializedClassMemberArrangement";
            }

            int addedCount = 0;
            for (String studentId : studentIds) {
                if (!studentsSpecializedClassesService.isStudentAlreadyRequiredForClass(studentId, classId)) {
                    Students student = studentsService.getStudentById(studentId);
                    if (student != null) {
                        Students_SpecializedClasses ssc = new Students_SpecializedClasses();
                        ssc.setStudent(student);
                        ssc.setSpecializedClass(classEntity);
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
                model.addAttribute("errorMessage", String.join("; ", errors));
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsSpecializedClassesService.getStudentsByClass(classEntity));
                String subjectId = classEntity.getSpecializedSubject().getSubjectId();
                model.addAttribute("studentsNotInClass", studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "SpecializedClassMemberArrangement";
            }

            redirectAttributes.addFlashAttribute("successMessage", addedCount + " student(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while adding students: " + e.getMessage());
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new SpecializedClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            String subjectId = classEntity != null ? classEntity.getSpecializedSubject().getSubjectId() : "";
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
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
                model.addAttribute("errorMessage", "Class not found");
                model.addAttribute("class", new SpecializedClasses());
                model.addAttribute("studentsInClass", new ArrayList<Students>());
                model.addAttribute("studentsNotInClass", new ArrayList<Students>());
                model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
                model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
                return "SpecializedClassMemberArrangement";
            }

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for removal");
                model.addAttribute("errorMessage", "No lecturers selected for removal");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsSpecializedClassesService.getStudentsByClass(classEntity));
                String subjectId = classEntity.getSpecializedSubject().getSubjectId();
                model.addAttribute("studentsNotInClass", studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "SpecializedClassMemberArrangement";
            }

            lecturersClassesService.removeLecturerFromClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) removed successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while removing lecturers: " + e.getMessage());
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new SpecializedClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            String subjectId = classEntity != null ? classEntity.getSpecializedSubject().getSubjectId() : "";
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
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
                model.addAttribute("errorMessage", "Class not found");
                model.addAttribute("class", new SpecializedClasses());
                model.addAttribute("studentsInClass", new ArrayList<Students>());
                model.addAttribute("studentsNotInClass", new ArrayList<Students>());
                model.addAttribute("lecturersInClass", new ArrayList<MajorLecturers>());
                model.addAttribute("lecturersNotInClass", new ArrayList<MajorLecturers>());
                return "SpecializedClassMemberArrangement";
            }

            if (lecturerIds == null || lecturerIds.isEmpty()) {
                errors.add("No lecturers selected for assignment");
                model.addAttribute("errorMessage", "No lecturers selected for assignment");
                model.addAttribute("class", classEntity);
                model.addAttribute("studentsInClass", studentsSpecializedClassesService.getStudentsByClass(classEntity));
                String subjectId = classEntity.getSpecializedSubject().getSubjectId();
                model.addAttribute("studentsNotInClass", studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId));
                model.addAttribute("lecturersInClass", lecturersClassesService.listLecturersInClass(classEntity));
                model.addAttribute("lecturersNotInClass", lecturersClassesService.listLecturersNotInClass(classEntity));
                return "SpecializedClassMemberArrangement";
            }

            // Add lecturers to class
            lecturersClassesService.addLecturersToClass(classEntity, lecturerIds);

            redirectAttributes.addFlashAttribute("successMessage", lecturerIds.size() + " lecturer(s) assigned successfully!");
            session.setAttribute("currentClassId", classId);
            return "redirect:/staff-home/specialized-classes-list/member-arrangement";
        } catch (Exception e) {
            errors.add("An error occurred while adding lecturers: " + e.getMessage());
            SpecializedClasses classEntity = specializedClassesService.getClassById(classId);
            model.addAttribute("errorMessage", String.join("; ", errors));
            model.addAttribute("class", classEntity != null ? classEntity : new SpecializedClasses());
            model.addAttribute("studentsInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsByClass(classEntity) : new ArrayList<Students>());
            String subjectId = classEntity != null ? classEntity.getSpecializedSubject().getSubjectId() : "";
            model.addAttribute("studentsNotInClass", classEntity != null ? studentsSpecializedClassesService.getStudentsNotInClassAndSubject(classId, subjectId) : new ArrayList<Students>());
            model.addAttribute("lecturersInClass", classEntity != null ? lecturersClassesService.listLecturersInClass(classEntity) : new ArrayList<MajorLecturers>());
            model.addAttribute("lecturersNotInClass", classEntity != null ? lecturersClassesService.listLecturersNotInClass(classEntity) : new ArrayList<MajorLecturers>());
            return "SpecializedClassMemberArrangement";
        }
    }
}