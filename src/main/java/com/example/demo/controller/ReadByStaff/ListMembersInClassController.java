package com.example.demo.controller.ReadByStaff;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.MajorLecturers;
import com.example.demo.entity.MajorLecturers_MajorClasses;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_MajorClasses;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        MajorClasses selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            model.addAttribute("errorMessage", "Class not found.");
            return "MemberArrangement";
        }

        // Lưu classId vào session
        session.setAttribute("currentClassId", classId);

        // Lấy danh sách sinh viên và giảng viên
        List<Students_MajorClasses> studentsInClassRecords = studentsClassesService.listStudentsInClass(selectedClass);
        List<Students> studentsNotInClass = studentsClassesService.listStudentsNotInClass(selectedClass);
        List<MajorLecturers_MajorClasses> lecturersInClassRecords = lecturersClassesService.listLecturersInClass(selectedClass);
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(selectedClass);
        // Lấy danh sách theo yêu cầu
        List<Students> studentsFailedNotPaid = studentsClassesService.listStudentsFailedSubjectAndNotPaid(selectedClass);
        List<Students> studentsFailedPaid = studentsClassesService.listStudentsFailedSubjectAndPaid(selectedClass);
        List<Students> studentsNotTakenPaid = studentsClassesService.listStudentsNotTakenSubject(selectedClass, true);
        List<Students> studentsNotTakenNotPaid = studentsClassesService.listStudentsNotTakenSubject(selectedClass, false);
        List<Students> studentsCurrentlyTaking = studentsClassesService.listStudentsCurrentlyTakingSubject(selectedClass);
        List<Students> studentsCompletedPrevSemesterWithSufficientBalance = studentsClassesService.listStudentsCompletedPreviousSemesterWithSufficientBalance(selectedClass);
        List<Students> studentsCompletedPrevSemesterWithInsufficientBalance = studentsClassesService.listStudentsCompletedPreviousSemesterWithInsufficientBalance(selectedClass);

        // Ánh xạ sang Students và Lecturers
        List<Students> studentsInClass = studentsInClassRecords.stream()
                .map(Students_MajorClasses::getStudent)
                .filter(student -> student != null)
                .collect(Collectors.toList());
        List<MajorLecturers> lecturersInClass = lecturersInClassRecords.stream()
                .map(MajorLecturers_MajorClasses::getLecturer)
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
        model.addAttribute("studentsCompletedPrevSemesterWithSufficientBalance", studentsCompletedPrevSemesterWithSufficientBalance.isEmpty() ? Collections.emptyList() : studentsCompletedPrevSemesterWithSufficientBalance);
        model.addAttribute("studentsCompletedPrevSemesterWithInsufficientBalance", studentsCompletedPrevSemesterWithInsufficientBalance.isEmpty() ? Collections.emptyList() : studentsCompletedPrevSemesterWithInsufficientBalance);
        model.addAttribute("addStudentForm", new Object());
        model.addAttribute("addLecturerForm", new Object());

        return "MemberArrangement";
    }

    @GetMapping("/member-arrangement")
    public String memberArrangement(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String classId = (String) session.getAttribute("currentClassId");

        if (classId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "No class selected. Please select a class.");
            return "redirect:/staff-home/classes-list";
        }

        MajorClasses selectedClass = classesService.getClassById(classId);
        if (selectedClass == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Class not found.");
            session.removeAttribute("currentClassId"); // Clear stale session data
            return "redirect:/staff-home/classes-list";
        }

        // Lấy danh sách sinh viên và giảng viên
        List<Students_MajorClasses> studentsInClassRecords = studentsClassesService.listStudentsInClass(selectedClass);
        List<Students> studentsNotInClass = studentsClassesService.listStudentsNotInClass(selectedClass);
        List<MajorLecturers_MajorClasses> lecturersInClassRecords = lecturersClassesService.listLecturersInClass(selectedClass);
        List<MajorLecturers> lecturersNotInClass = lecturersClassesService.listLecturersNotInClass(selectedClass);

        // Lấy danh sách theo yêu cầu
        List<Students> studentsFailedNotPaid = studentsClassesService.listStudentsFailedSubjectAndNotPaid(selectedClass);
        List<Students> studentsFailedPaid = studentsClassesService.listStudentsFailedSubjectAndPaid(selectedClass);
        List<Students> studentsNotTakenPaid = studentsClassesService.listStudentsNotTakenSubject(selectedClass, true);
        List<Students> studentsNotTakenNotPaid = studentsClassesService.listStudentsNotTakenSubject(selectedClass, false);
        List<Students> studentsCurrentlyTaking = studentsClassesService.listStudentsCurrentlyTakingSubject(selectedClass);
        List<Students> studentsCompletedPrevSemesterWithSufficientBalance = studentsClassesService.listStudentsCompletedPreviousSemesterWithSufficientBalance(selectedClass);
        List<Students> studentsCompletedPrevSemesterWithInsufficientBalance = studentsClassesService.listStudentsCompletedPreviousSemesterWithInsufficientBalance(selectedClass);

        // Ánh xạ sang Students và Lecturers
        List<Students> studentsInClass = studentsInClassRecords.stream()
                .map(Students_MajorClasses::getStudent)
                .filter(student -> student != null)
                .collect(Collectors.toList());
        List<MajorLecturers> lecturersInClass = lecturersInClassRecords.stream()
                .map(MajorLecturers_MajorClasses::getLecturer)
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
        model.addAttribute("studentsCompletedPrevSemesterWithSufficientBalance", studentsCompletedPrevSemesterWithSufficientBalance.isEmpty() ? Collections.emptyList() : studentsCompletedPrevSemesterWithSufficientBalance);
        model.addAttribute("studentsCompletedPrevSemesterWithInsufficientBalance", studentsCompletedPrevSemesterWithInsufficientBalance.isEmpty() ? Collections.emptyList() : studentsCompletedPrevSemesterWithInsufficientBalance);

        return "MemberArrangement";
    }
}