package com.example.demo.students_Classes.students_MajorClass.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model.StudentRequiredMajorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.service.StudentRequiredMajorSubjectsService;
import com.example.demo.students_Classes.abstractStudents_Class.model.StudentsClassesId;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.students_Classes.students_MajorClass.model.Students_MajorClasses;
import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
import com.example.demo.retakeSubjects.model.RetakeSubjects;
import com.example.demo.retakeSubjects.service.RetakeSubjectsService;
import com.example.demo.accountBalance.service.AccountBalancesService;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
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
public class StudentMemberController {

    private final StudentsMajorClassesService studentsMajorClassesService;
    private final MajorClassesService classesService;
    private final StaffsService staffsService;
    private final StudentsService studentsService;
    private final RetakeSubjectsService retakeSubjectsService;
    private final AccountBalancesService accountBalancesService;
    private final TuitionByYearService tuitionByYearService;
    private final StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService;

    @Autowired
    public StudentMemberController(
            StudentsMajorClassesService studentsMajorClassesService,
            MajorClassesService classesService,
            StaffsService staffsService,
            StudentsService studentsService,
            RetakeSubjectsService retakeSubjectsService,
            AccountBalancesService accountBalancesService,
            TuitionByYearService tuitionByYearService,
            StudentRequiredMajorSubjectsService studentRequiredMajorSubjectsService) {
        this.studentsMajorClassesService = studentsMajorClassesService;
        this.classesService = classesService;
        this.staffsService = staffsService;
        this.studentsService = studentsService;
        this.retakeSubjectsService = retakeSubjectsService;
        this.accountBalancesService = accountBalancesService;
        this.tuitionByYearService = tuitionByYearService;
        this.studentRequiredMajorSubjectsService = studentRequiredMajorSubjectsService;
    }

    @GetMapping("/member-arrangement/students")
    public String showStudentArrangement(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("currentClassId");
        if (classId == null) {
            return handleNoClassSelected(model);
        }
        MajorClasses clazz = classesService.getClassById(classId);
        if (clazz == null) {
            return handleNoClassSelected(model);
        }

        List<Students> studentsInClass = studentsMajorClassesService.getStudentsByClass(clazz);

        String subjectId = clazz.getSubject().getSubjectId();
        List<RetakeSubjects> retakeList = retakeSubjectsService.getRetakeSubjectsBySubjectId(subjectId);

        List<StudentRequiredMajorSubjects> requiredList = studentRequiredMajorSubjectsService
                .getStudentRequiredMajorSubjects(clazz.getSubject(), null);
        List<Students> requiredStudents = requiredList.stream()
                .map(StudentRequiredMajorSubjects::getStudent)
                .filter(Objects::nonNull)
                .toList();

        List<Students> studentsWithEnoughMoney = retakeSubjectsService.getStudentsWithSufficientBalance(subjectId, requiredStudents);
        List<Students> studentsDoNotHaveEnoughMoney = retakeSubjectsService.getStudentsWithInsufficientBalance(subjectId, requiredStudents);

        model.addAttribute("class", clazz);
        model.addAttribute("studentsInClass", studentsInClass);
        model.addAttribute("studentsWithEnoughMoney", studentsWithEnoughMoney);
        model.addAttribute("studentsDoNotHaveEnoughMoney", studentsDoNotHaveEnoughMoney);
        model.addAttribute("retakeList", retakeList);

        return "MemberArrangement"; // View riêng cho sinh viên
    }

    @PostMapping("/member-arrangement/select-class")
    public String selectClass(@RequestParam("classId") String classId,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (classesService.getClassById(classId) == null) {
            ra.addFlashAttribute("errorMessage", "Class not found");
            return "redirect:/staff-home/classes-list";
        }
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement/students";
    }

    // XÓA SINH VIÊN KHỎI LỚP
    @PostMapping("/remove-student-from-class")
    public String removeStudent(@RequestParam("classId") String classId,
                                @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                                Model model, RedirectAttributes ra, HttpSession session) {
        List<String> errors = new ArrayList<>();
        MajorClasses clazz = classesService.getClassById(classId);
        if (clazz == null) {
            errors.add("Class not found");
            populateError(model, null, errors);
            return "MemberArrangement";
        }
        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }

        String subjectId = clazz.getSubject().getSubjectId();
        for (String sid : studentIds) {
            if (studentsMajorClassesService.existsByStudentAndClass(sid, classId)) {
                studentsMajorClassesService.removeStudentFromClass(sid, classId);
            }
            if (!retakeSubjectsService.existsByStudentAndSubject(sid, subjectId)) {
                Students s = studentsService.getStudentById(sid);
                if (s != null) {
                    RetakeSubjects r = new RetakeSubjects();
                    r.setId(new StudentRetakeSubjectsId(sid, subjectId));
                    r.setStudent(s);
                    r.setSubject(clazz.getSubject());
                    r.setRetakeReason("Removed from class");
                    r.setCreatedAt(LocalDateTime.now());
                    retakeSubjectsService.save(r);
                }
            }
        }
        ra.addFlashAttribute("successMessage", "Students removed and added to retake list.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement/students";
    }

    // THÊM SINH VIÊN VÀO LỚP (từ danh sách đủ tiền)
    @PostMapping("/add-students-to-class")
    public String addStudent(@RequestParam("classId") String classId,
                             @RequestParam(value = "studentIds", required = false) List<String> studentIds,
                             Model model, RedirectAttributes ra, HttpSession session) {
        List<String> errors = new ArrayList<>();
        MajorClasses clazz = classesService.getClassById(classId);
        if (clazz == null) {
            errors.add("Class not found");
            populateError(model, null, errors);
            return "MemberArrangement";
        }
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            errors.add("Staff not found");
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }
        if (studentIds == null || studentIds.isEmpty()) {
            errors.add("Please select at least one student");
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }

        String subjectId = clazz.getSubject().getSubjectId();
        int added = 0;
        for (String sid : studentIds) {
            Students s = studentsService.getStudentById(sid);
            if (s == null) continue;

            Double fee = getReStudyFee(subjectId, s);
            if (fee == null || fee <= 0) {
                errors.add("Fee not defined for subject: " + s.getFullName());
                continue;
            }
            if (!accountBalancesService.hasSufficientBalance(sid, fee)) {
                errors.add(s.getFullName() + " does not have enough money");
                continue;
            }
            if (studentsMajorClassesService.existsByStudentAndClass(sid, classId)) {
                errors.add(s.getFullName() + " already in class");
                continue;
            }

            Students_MajorClasses smc = new Students_MajorClasses();
            smc.setId(new StudentsClassesId(classId, sid));
            smc.setStudent(s);
            smc.setMajorClass(clazz);
            smc.setAddedBy(staff);
            smc.setCreatedAt(LocalDateTime.now());
            studentsMajorClassesService.addStudentToClass(smc);
            added++;

            retakeSubjectsService.deductAndLogPayment(s, subjectId, fee);
        }

        if (!errors.isEmpty()) {
            populateError(model, clazz, errors);
            return "MemberArrangement";
        }
        ra.addFlashAttribute("successMessage", added + " student(s) added and payment deducted.");
        session.setAttribute("currentClassId", classId);
        return "redirect:/staff-home/classes-list/member-arrangement/students";
    }

    // Helper: Lỗi
    private void populateError(Model model, MajorClasses clazz, List<String> errors) {
        model.addAttribute("errorMessage", String.join("; ", errors));
        model.addAttribute("class", clazz != null ? clazz : new MajorClasses());
        if (clazz != null) {
            model.addAttribute("studentsInClass", studentsMajorClassesService.getStudentsByClass(clazz));
        } else {
            model.addAttribute("studentsInClass", new ArrayList<>());
        }
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
    }

    // Helper: Phí học lại
    private Double getReStudyFee(String subjectId, Students student) {
        Integer year = student.getAdmissionYear();
        if (year == null || student.getCampus() == null) return null;
        return tuitionByYearService.getTuitionsWithReStudyFeeByYear(year, student.getCampus()).stream()
                .filter(t -> t.getSubject().getSubjectId().equals(subjectId))
                .map(t -> t.getReStudyTuition())
                .findFirst()
                .orElse(null);
    }

    private String handleNoClassSelected(Model model) {
        model.addAttribute("errorMessage", "No class selected");
        model.addAttribute("class", new MajorClasses());
        model.addAttribute("studentsInClass", new ArrayList<>());
        model.addAttribute("studentsWithEnoughMoney", new ArrayList<>());
        model.addAttribute("studentsDoNotHaveEnoughMoney", new ArrayList<>());
        return "MemberArrangement";
    }
}