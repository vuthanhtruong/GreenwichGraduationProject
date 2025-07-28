package com.example.demo.controller.Read;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Lecturers_Classes;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_Classes;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff-home/classes-list")
@PreAuthorize("hasRole('STAFF')")
public class ListMembersInClassController {

    private final Students_ClassesService studentsClassesService;
    private final Lecturers_ClassesService lecturersClassesService;
    private final ClassesService classesService;
    private final StudentsService studentsService;
    private final LecturesService lecturersService;

    @Autowired
    public ListMembersInClassController(Students_ClassesService studentsClassesService,
                                        Lecturers_ClassesService lecturersClassesService,
                                        ClassesService classesService,
                                        StudentsService studentsService,
                                        LecturesService lecturersService) {
        this.studentsClassesService = studentsClassesService;
        this.lecturersClassesService = lecturersClassesService;
        this.classesService = classesService;
        this.studentsService = studentsService;
        this.lecturersService = lecturersService;
    }

    @PostMapping("/member-arrangement")
    public String memberArrangement(@RequestParam("id") String classId,
                                    Model model, HttpSession session) {
        Classes selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            model.addAttribute("errorMessage", "Class not found.");
            return "MemberArrangement";
        }

        // Lưu classId vào session
        session.setAttribute("currentClassId", classId);

        // Lấy danh sách sinh viên và giảng viên
        List<Students_Classes> studentsInClassRecords = studentsClassesService.listStudentsInClass(selectedClass);
        List<Students> studentsNotInClass = studentsClassesService.listStudentsNotInClass(selectedClass);
        List<Lecturers_Classes> lecturersInClassRecords = lecturersClassesService.listLecturersInClass(selectedClass);
        List<Lecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(selectedClass);

        // Lấy danh sách theo yêu cầu
        List<Students> studentsFailedNotPaid = studentsClassesService.listStudentsFailedSubjectAndNotPaid(selectedClass);
        List<Students> studentsFailedPaid = studentsClassesService.listStudentsFailedSubjectAndPaid(selectedClass);
        List<Students> studentsNotTakenPaid = studentsClassesService.listStudentsNotTakenSubject(selectedClass, true);
        List<Students> studentsNotTakenNotPaid = studentsClassesService.listStudentsNotTakenSubject(selectedClass, false);
        List<Students> studentsCurrentlyTaking = studentsClassesService.listStudentsCurrentlyTakingSubject(selectedClass);
        List<Students> studentsCompletedPrevSemester = studentsClassesService.listStudentsCompletedPreviousSemester(selectedClass);

        // Ánh xạ sang Students và Lecturers
        List<Students> studentsInClass = studentsInClassRecords.stream()
                .map(Students_Classes::getStudent)
                .filter(student -> student != null)
                .collect(Collectors.toList());
        List<Lecturers> lecturersInClass = lecturersInClassRecords.stream()
                .map(Lecturers_Classes::getLecturer)
                .filter(lecturer -> lecturer != null)
                .collect(Collectors.toList());

        // Thêm dữ liệu vào model
        model.addAttribute("class", selectedClass);
        model.addAttribute("studentsInClass", studentsInClass.isEmpty() ? Collections.emptyList() : studentsInClass);
        model.addAttribute("studentsNotInClass", studentsNotInClass.isEmpty() ? Collections.emptyList() : studentsNotInClass);
        model.addAttribute("lecturersInClass", lecturersInClass.isEmpty() ? Collections.emptyList() : lecturersInClass);
        model.addAttribute("lecturersNotInClass", lecturersNotInClass.isEmpty() ? Collections.emptyList() : lecturersNotInClass);
        model.addAttribute("studentsFailedNotPaid", studentsFailedNotPaid.isEmpty() ? Collections.emptyList() : studentsFailedNotPaid);
        model.addAttribute("studentsFailedPaid", studentsFailedPaid.isEmpty() ? Collections.emptyList() : studentsFailedPaid);
        model.addAttribute("studentsNotTakenPaid", studentsNotTakenPaid.isEmpty() ? Collections.emptyList() : studentsNotTakenPaid);
        model.addAttribute("studentsNotTakenNotPaid", studentsNotTakenNotPaid.isEmpty() ? Collections.emptyList() : studentsNotTakenNotPaid);
        model.addAttribute("studentsCurrentlyTaking", studentsCurrentlyTaking.isEmpty() ? Collections.emptyList() : studentsCurrentlyTaking);
        model.addAttribute("studentsCompletedPrevSemester", studentsCompletedPrevSemester.isEmpty() ? Collections.emptyList() : studentsCompletedPrevSemester);
        model.addAttribute("addStudentForm", new Object());
        model.addAttribute("addLecturerForm", new Object());

        return "MemberArrangement";
    }

    @PostMapping("/add-students")
    public String addStudentsToClass(@RequestParam("classId") String classId,
                                     @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                     RedirectAttributes redirectAttributes) {
        Classes selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found.");
            return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
        }

        if (studentIds == null || studentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No students selected.");
            return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
        }

        try {
            for (String studentId : studentIds) {
                Students student = studentsService.getStudentById(studentId);
                if (student != null) {
                    // Kiểm tra xem sinh viên đã có trong lớp chưa
                    boolean alreadyInClass = studentsClassesService.listStudentsInClass(selectedClass)
                            .stream()
                            .anyMatch(sc -> sc.getStudent().getId().equals(studentId));
                    if (!alreadyInClass) {
                        Students_Classes studentClass = new Students_Classes();
                        studentClass.setClassEntity(selectedClass);
                        studentClass.setStudent(student);
                        studentsClassesService.addStudentToClass(studentClass);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Students added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add students: " + e.getMessage());
        }

        return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
    }

    @PostMapping("/add-lecturers")
    public String addLecturersToClass(@RequestParam("classId") String classId,
                                      @RequestParam(value = "lecturerIds", required = false) List<String> lecturerIds,
                                      RedirectAttributes redirectAttributes) {
        Classes selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found.");
            return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
        }

        if (lecturerIds == null || lecturerIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No lecturers selected.");
            return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
        }

        try {
            for (String lecturerId : lecturerIds) {
                Lecturers lecturer = lecturersService.getLecturerById(lecturerId);
                if (lecturer != null) {
                    // Kiểm tra xem giảng viên đã có trong lớp chưa
                    boolean alreadyInClass = lecturersClassesService.listLecturersInClass(selectedClass)
                            .stream()
                            .anyMatch(lc -> lc.getLecturer().getId().equals(lecturerId));
                    if (!alreadyInClass) {
                        Lecturers_Classes lecturerClass = new Lecturers_Classes();
                        lecturerClass.setClassEntity(selectedClass);
                        lecturerClass.setLecturer(lecturer);
                        lecturersClassesService.addLecturerToClass(lecturerClass);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lecturers added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add lecturers: " + e.getMessage());
        }

        return "redirect:/staff-home/classes-list/member-arrangement?id=" + classId;
    }
}